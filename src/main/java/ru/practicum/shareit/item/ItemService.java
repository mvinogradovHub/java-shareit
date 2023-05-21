package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingWithoutObjDto;
import ru.practicum.shareit.exception.BadDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        return ItemMapper.ItemToItemDto(itemRepository.save(ItemMapper.ItemDtoToItem(itemDto,user)),null,null,null);
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
        return ItemMapper.ItemToItemDto(itemRepository.save(item),null,null,null);
    }

    public ItemDto getItem(Long userId, Long itemId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item with ID " + itemId + " not found"));
        BookingWithoutObjDto lastBookingWithoutObjDto = null;
        BookingWithoutObjDto nextBookingWithoutObjDto = null;
        List<CommentDto> commentsDto = CommentMapper.ListCommentToListCommentDto(commentRepository.getCommentsItem(itemId));
        if (item.getOwner().getId().equals(userId)) {
            lastBookingWithoutObjDto = BookingMapper
                    .BookingToBookingWithoutObjDto(bookingRepository
                            .getItemLastBooking(item.getId(), LocalDateTime.now()).orElse(null));
            nextBookingWithoutObjDto = BookingMapper
                    .BookingToBookingWithoutObjDto(bookingRepository
                            .getItemNextBooking(item.getId(), LocalDateTime.now()).orElse(null));
        }
        return ItemMapper.ItemToItemDto(item, lastBookingWithoutObjDto, nextBookingWithoutObjDto, commentsDto);
    }

    public List<ItemDto> getItems(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId);
        List<ItemDto> itemWithBookingAndCommentDto = new ArrayList<>();
        for (Item item : items) {
            Long id = item.getId();
            List<CommentDto> commentsDto = CommentMapper.ListCommentToListCommentDto(commentRepository.getCommentsItem(id));
            BookingWithoutObjDto lastBookingWithoutObjDto = BookingMapper
                    .BookingToBookingWithoutObjDto(bookingRepository
                            .getItemLastBooking(id, LocalDateTime.now()).orElse(null));
            BookingWithoutObjDto nextBookingWithoutObjDto = BookingMapper
                    .BookingToBookingWithoutObjDto(bookingRepository
                            .getItemNextBooking(id, LocalDateTime.now()).orElse(null));
            itemWithBookingAndCommentDto.add(ItemMapper
                    .ItemToItemDto(item, lastBookingWithoutObjDto, nextBookingWithoutObjDto, commentsDto));
        }
        return itemWithBookingAndCommentDto;
    }

    public List<ItemDto> searchItems(Long userId, String text) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return ItemMapper.ListItemToListItemDto(itemRepository.searchItems(text));
    }

    public CommentDto addComment(CommentDto commentDto, Long userId, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item with ID " + itemId + " not found"));
        bookingRepository.getByBookerAndItemPastApprovedBooking(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new BadDataException("The user id " + userId + " did not book the item id " + itemId));
        commentDto.setCreated(LocalDateTime.now());
        return CommentMapper.CommentToCommentDto(commentRepository.save(CommentMapper.CommentDtoToComment(commentDto, user, item)));
    }


}
