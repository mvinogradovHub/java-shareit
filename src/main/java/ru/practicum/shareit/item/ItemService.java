package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingWithoutObjDto;
import ru.practicum.shareit.exception.BadDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
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
  private final UserRepository userRepository;
  private final BookingRepository bookingRepository;
  private final CommentRepository commentRepository;
  private final ItemRequestRepository itemRequestRepository;

  public ItemDto addItem(ItemDto itemDto, Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
    Long itemRequestId = itemDto.getRequestId();
    if (itemRequestId != null) {
      ItemRequestDto itemRequestDto =
          ItemRequestMapper.itemRequestToItemRequestDto(
              itemRequestRepository
                  .findById(itemRequestId)
                  .orElseThrow(
                      () ->
                          new NotFoundException(
                              "Item request ID " + itemRequestId + " not found")));
      return ItemMapper.itemToItemDto(
          itemRepository.save(ItemMapper.itemDtoToItem(itemDto, user, itemRequestDto)),
          null,
          null,
          null);
    }
    return ItemMapper.itemToItemDto(
        itemRepository.save(ItemMapper.itemDtoToItem(itemDto, user, null)), null, null, null);
  }

  public ItemDto updateItem(ItemDto itemDto, Long userId, Long itemId) {
    userRepository
        .findById(userId)
        .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
    Item item =
        itemRepository
            .findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item with ID " + itemId + " not found"));
    if (!item.getOwner().getId().equals(userId)) {
      log.warn("The user id {} does not have rights to edit items", userId);
      throw new NotFoundException("User id " + userId + " does not have rights to edit item");
    }
    if (itemDto.getName() != null) {
      item.setName(itemDto.getName());
    }
    if (itemDto.getDescription() != null) {
      item.setDescription(itemDto.getDescription());
    }
    if (itemDto.getAvailable() != null) {
      item.setAvailable(itemDto.getAvailable());
    }
    return ItemMapper.itemToItemDto(itemRepository.save(item), null, null, null);
  }

  public ItemDto getItem(Long userId, Long itemId) {
    userRepository
        .findById(userId)
        .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
    Item item =
        itemRepository
            .findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item with ID " + itemId + " not found"));
    BookingWithoutObjDto lastBookingWithoutObjDto = null;
    BookingWithoutObjDto nextBookingWithoutObjDto = null;
    List<CommentDto> commentsDto =
        CommentMapper.listCommentToListCommentDto(commentRepository.findByItemId(itemId));
    if (item.getOwner().getId().equals(userId)) {
      lastBookingWithoutObjDto =
          BookingMapper.bookingToBookingWithoutObjDto(
              bookingRepository.getItemLastBooking(item.getId(), LocalDateTime.now()).orElse(null));
      nextBookingWithoutObjDto =
          BookingMapper.bookingToBookingWithoutObjDto(
              bookingRepository.getItemNextBooking(item.getId(), LocalDateTime.now()).orElse(null));
    }
    return ItemMapper.itemToItemDto(
        item, lastBookingWithoutObjDto, nextBookingWithoutObjDto, commentsDto);
  }

  public List<ItemDto> getItems(Long userId, Integer start, Integer size) {
    userRepository
        .findById(userId)
        .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
    Pageable page = convertToPageSettings(start, size, "id");
    List<Item> items = itemRepository.findByOwnerId(userId, page);
    List<ItemDto> itemWithBookingAndCommentDto = new ArrayList<>();
    for (Item item : items) {
      Long id = item.getId();
      List<CommentDto> commentsDto =
          CommentMapper.listCommentToListCommentDto(commentRepository.findByItemId(id));
      BookingWithoutObjDto lastBookingWithoutObjDto =
          BookingMapper.bookingToBookingWithoutObjDto(
              bookingRepository.getItemLastBooking(id, LocalDateTime.now()).orElse(null));
      BookingWithoutObjDto nextBookingWithoutObjDto =
          BookingMapper.bookingToBookingWithoutObjDto(
              bookingRepository.getItemNextBooking(id, LocalDateTime.now()).orElse(null));
      itemWithBookingAndCommentDto.add(
          ItemMapper.itemToItemDto(
              item, lastBookingWithoutObjDto, nextBookingWithoutObjDto, commentsDto));
    }
    return itemWithBookingAndCommentDto;
  }

  public List<ItemDto> searchItems(Long userId, String text, Integer start, Integer size) {
    userRepository
        .findById(userId)
        .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
    if (text.isBlank()) {
      return Collections.emptyList();
    }
    Pageable page = convertToPageSettings(start, size, "id");
    return ItemMapper.listItemToListItemDto(itemRepository.searchItems(text, page));
  }

  public CommentDto addComment(CommentDto commentDto, Long userId, Long itemId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
    Item item =
        itemRepository
            .findById(itemId)
            .orElseThrow(() -> new NotFoundException("Item with ID " + itemId + " not found"));
    bookingRepository
        .getByBookerAndItemPastApprovedBooking(userId, itemId, LocalDateTime.now())
        .orElseThrow(
            () ->
                new BadDataException(
                    "The user id " + userId + " did not book the item id " + itemId));
    commentDto.setCreated(LocalDateTime.now());
    return CommentMapper.commentToCommentDto(
        commentRepository.save(CommentMapper.commentDtoToComment(commentDto, user, item)));
  }

  public Pageable convertToPageSettings(Integer start, Integer size, String sort) {
    int page = start >= 0 ? Math.round((float) start / size) : -1;
    return PageRequest.of(page, size, Sort.by(sort));
  }
}
