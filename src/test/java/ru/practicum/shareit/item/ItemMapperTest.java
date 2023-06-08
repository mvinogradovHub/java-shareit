package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingWithoutObjDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {
    private BookingWithoutObjDto booking;
    private ItemRequest itemRequest;
    private User user;
    private Item item;
    private ItemDto itemDto;
    private List<CommentDto> comments;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {
        booking = BookingWithoutObjDto.builder()
                .itemId(1L)
                .end(LocalDateTime.of(2023, 1, 10, 0, 0, 0, 0))
                .start(LocalDateTime.of(2023, 2, 10, 0, 0, 0, 0))
                .bookerId(1L)
                .status(BookingStatus.APPROVED)
                .id(1L)
                .build();

        itemRequest = ItemRequest.builder()
                .id(5L)
                .created(LocalDateTime.of(2023, 3, 10, 0, 0, 0, 0))
                .description("Описание запроса")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(6L)
                .created(LocalDateTime.of(2023, 8, 10, 0, 0, 0, 0))
                .description("Описание запроса")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("mail@mail.ru")
                .name("Misha")
                .build();

        user = User.builder()
                .id(2L)
                .email("mail@mail.ru")
                .name("Misha")
                .build();

        item = Item.builder()
                .id(1L)
                .owner(user)
                .name("Ответртка")
                .available(true)
                .description("Описание")
                .request(itemRequest)
                .build();

        itemDto = ItemDto.builder()
                .id(2L)
                .name("Ответртка2")
                .description("Описание2")
                .owner(userDto)
                .available(false)
                .requestId(5L)
                .lastBooking(booking)
                .nextBooking(booking)
                .comments(new ArrayList<>())
                .build();

        comments = List.of(CommentDto.builder()
                .created(LocalDateTime.of(2023, 4, 10, 0, 0, 0, 0))
                .text("Текст")
                .id(1L)
                .build());
    }

    @Test
    void itemToItemDto_whenSendItemWithParametersIsNull_thenReturnLikeItemDto() {
        item.setRequest(null);

        ItemDto newItemDto = ItemMapper.itemToItemDto(item, null, null, null);

        assertNull(newItemDto.getRequestId());
        assertNull(newItemDto.getLastBooking());
        assertNull(newItemDto.getNextBooking());
        assertNull(newItemDto.getComments());
    }

    @Test
    void itemToItemDto_whenSendItem_thenReturnLikeItemDto() {
        ItemDto newItemDto = ItemMapper.itemToItemDto(item, booking, booking, comments);

        assertEquals(newItemDto.getId(), item.getId());
        assertEquals(newItemDto.getLastBooking(), booking);
        assertEquals(newItemDto.getNextBooking(), booking);
        assertEquals(newItemDto.getComments(), comments);
        assertEquals(newItemDto.getRequestId(), itemRequest.getId());
        assertEquals(newItemDto.getLastBooking(), booking);
        assertEquals(newItemDto.getName(), "Ответртка");
        assertEquals(newItemDto.getComments(), comments);
        assertEquals(newItemDto.getAvailable(), true);
        assertEquals(newItemDto.getDescription(), "Описание");
        assertEquals(newItemDto.getOwner().getId(), user.getId());
    }

    @Test
    void itemDtoToItem_whenSendItemDtoWithItemRequestDtoIsNull_thenReturnLikeItem() {
        Item newItem = ItemMapper.itemDtoToItem(itemDto, user, null);

        assertEquals(newItem.getId(), itemDto.getId());
        assertNull(newItem.getRequest());
    }

    @Test
    void itemDtoToItem_whenSendItemDto_thenReturnLikeItem() {
        Item newItem = ItemMapper.itemDtoToItem(itemDto, user, itemRequestDto);

        assertEquals(newItem.getId(), itemDto.getId());
        assertEquals(newItem.getName(), itemDto.getName());
        assertEquals(newItem.getDescription(), itemDto.getDescription());
        assertEquals(newItem.getOwner(), user);
        assertEquals(newItem.getAvailable(), false);
        assertEquals(newItem.getRequest().getId(), itemRequestDto.getId());
    }

    @Test
    void listItemToListItemDto_whenSendListItem_thenReturnListItemDto() {
        List<Item> items = List.of(item);

        List<ItemDto> itemDtos = ItemMapper.listItemToListItemDto(items);

        assertEquals(items.get(0).getId(), itemDtos.get(0).getId());
    }
}
