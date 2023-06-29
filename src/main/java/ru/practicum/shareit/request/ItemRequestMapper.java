package ru.practicum.shareit.request;

import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto itemRequestToItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }
        List<ItemDto> items = itemRequest.getItems() == null ? null : itemRequest.getItems().stream()
                .map(ItemMapper::itemToItemDto)
                .collect(Collectors.toList());
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .items(items)
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest itemRequestDtoToItemRequest(ItemRequestDto itemRequestDto, User user) {
        if (itemRequestDto == null) {
            return null;
        }
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(user)
                .created(itemRequestDto.getCreated())
                .build();
    }

    public static List<ItemRequestDto> listItemRequestToListItemRequestDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(ItemRequestMapper::itemRequestToItemRequestDto)
                .collect(Collectors.toList());
    }
}
