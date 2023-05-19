package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingWithoutAttachObjDto;
import ru.practicum.shareit.exception.ErrorToCreateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto addItem(ItemDto itemDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, user)));
    }

    public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item with ID " + itemId + " not found"));
        itemValidator.checkItemOwner(userId, item);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemWithBookingAndCommentDto getItem(Long userId, Long itemId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item with ID " + itemId + " not found"));
        BookingWithoutAttachObjDto lastBookingWithoutAttachObjDto = null;
        BookingWithoutAttachObjDto nextBookingWithoutAttachObjDto = null;
        List<CommentDto> commentsDto = CommentMapper.toListCommentDto(commentRepository.getCommentsItem(itemId));
        if (item.getOwner().getId().equals(userId)) {
            lastBookingWithoutAttachObjDto = BookingMapper
                    .toBookingWithoutAttachObjDto(bookingRepository
                            .getItemLastBooking(item.getId(), LocalDateTime.now()).orElse(null));
            nextBookingWithoutAttachObjDto = BookingMapper
                    .toBookingWithoutAttachObjDto(bookingRepository
                            .getItemNextBooking(item.getId(), LocalDateTime.now()).orElse(null));
        }
        return ItemMapper.toItemWithBookingAndCommentDto(item, lastBookingWithoutAttachObjDto, nextBookingWithoutAttachObjDto, commentsDto);
    }

    public List<ItemWithBookingAndCommentDto> getItems(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId);
        List<ItemWithBookingAndCommentDto> itemWithBookingAndCommentDto = new ArrayList<>();
        for (Item item : items) {
            Long id = item.getId();
            List<CommentDto> commentsDto = CommentMapper.toListCommentDto(commentRepository.getCommentsItem(id));
            BookingWithoutAttachObjDto lastBookingWithoutAttachObjDto = BookingMapper
                    .toBookingWithoutAttachObjDto(bookingRepository
                            .getItemLastBooking(id, LocalDateTime.now()).orElse(null));
            BookingWithoutAttachObjDto nextBookingWithoutAttachObjDto = BookingMapper
                    .toBookingWithoutAttachObjDto(bookingRepository
                            .getItemNextBooking(id, LocalDateTime.now()).orElse(null));
            itemWithBookingAndCommentDto.add(ItemMapper
                    .toItemWithBookingAndCommentDto(item, lastBookingWithoutAttachObjDto, nextBookingWithoutAttachObjDto, commentsDto));
        }
        return itemWithBookingAndCommentDto;
    }

    public List<ItemDto> searchItems(Long userId, String text) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public CommentDto addComment(CommentDto commentDto, Long userId, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item with ID " + itemId + " not found"));
        bookingRepository.getByBookerAndItemPastApprovedBooking(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new ErrorToCreateException("The user id " + userId + " did not book the item id " + itemId));
        commentDto.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, user, item)));
    }


}
