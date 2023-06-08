package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {
    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {
        user = User.builder().id(2L).build();

        UserDto userDto = UserDto.builder().id(3L).build();

        Item item = Item.builder()
                .id(1L)
                .owner(user)
                .name("Ответртка")
                .available(true)
                .description("Описание")
                .request(itemRequest)
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(2L)
                .name("Ответртка2")
                .description("Описание2")
                .owner(userDto)
                .available(false)
                .requestId(5L)
                .comments(new ArrayList<>())
                .build();

        itemRequest = ItemRequest.builder()
                .id(5L)
                .description("Описание")
                .created(LocalDateTime.of(2023, 1, 10, 0, 0, 0, 0))
                .requestor(user)
                .items(List.of(item))
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(3L)
                .description("Описание")
                .created(LocalDateTime.of(2023, 3, 10, 0, 0, 0, 0))
                .requestorId(user.getId())
                .items(List.of(itemDto))
                .build();
    }

    @Test
    void itemRequestToItemRequestDto_whenSendNull_thenReturnNull() {
        assertNull(ItemRequestMapper.itemRequestToItemRequestDto(null));
    }

    @Test
    void itemRequestToItemRequestDto_whenSendItemRequest_thenReturnItemRequestDto() {
        ItemRequestDto newItemRequestDto = ItemRequestMapper.itemRequestToItemRequestDto(itemRequest);

        assertEquals(newItemRequestDto.getId(), itemRequest.getId());
        assertEquals(newItemRequestDto.getCreated(), itemRequest.getCreated());
        assertEquals(newItemRequestDto.getRequestorId(), itemRequest.getRequestor().getId());
        assertEquals(newItemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(newItemRequestDto.getItems().get(0).getId(), itemRequest.getItems().get(0).getId());
    }

    @Test
    void itemRequestDtoToItemRequest_whenSendNull_thenReturnNull() {
        assertNull(ItemRequestMapper.itemRequestDtoToItemRequest(null, user));
    }

    @Test
    void itemRequestDtoToItemRequest_whenSendItemRequestDto_thenReturnItemRequest() {
        ItemRequest newItemRequest = ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto, user);

        assertEquals(newItemRequest.getId(), itemRequestDto.getId());
        assertEquals(newItemRequest.getCreated(), itemRequestDto.getCreated());
        assertEquals(newItemRequest.getRequestor().getId(), itemRequestDto.getRequestorId());
        assertEquals(newItemRequest.getDescription(), itemRequestDto.getDescription());
        assertNull(newItemRequest.getItems());
    }

    @Test
    void listItemRequestToListItemRequestDto() {
        List<ItemRequest> itemRequests = List.of(itemRequest);

        List<ItemRequestDto> itemRequestDtoList = ItemRequestMapper.listItemRequestToListItemRequestDto(itemRequests);
        assertEquals(itemRequests.get(0).getId(), itemRequestDtoList.get(0).getId());
    }
}
