package ru.practicum.shareit.item;

import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.user.UserMapper;


public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(UserMapper.toUserDto(item.getOwner()))
                .available(item.getAvailable())
                .request(RequestMapper.toItemRequestDto(item.getRequest()))
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(UserMapper.toUser(itemDto.getOwner()))
                .available(itemDto.getAvailable())
                .request(RequestMapper.toItemRequest(itemDto.getRequest()))
                .build();
    }
}
