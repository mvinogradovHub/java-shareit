package ru.practicum.shareit.item;

import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto itemToItemDto(Item item) {
        Long itemRequestId = item.getRequest() != null ? item.getRequest().getId() : null;
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(UserMapper.userToUserDto(item.getOwner()))
                .available(item.getAvailable())
                .requestId(itemRequestId)
                .build();
    }


    public static Item itemDtoToItem(ItemDto itemDto, User user, ItemRequestDto itemRequestDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(user)
                .available(itemDto.getAvailable())
                .request(ItemRequestMapper.itemRequestDtoToItemRequest(itemRequestDto, user))
                .build();
    }

    public static List<ItemDto> listItemToListItemDto(List<Item> items) {
        return items.stream()
                .map(ItemMapper::itemToItemDto)
                .collect(Collectors.toList());
    }
}
