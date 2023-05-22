package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingWithoutObjDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;


public class ItemMapper {

    public static ItemDto itemToItemDto(Item item, BookingWithoutObjDto lastBooking, BookingWithoutObjDto nextBooking, List<CommentDto> commentDtoList) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(UserMapper.toUserDto(item.getOwner()))
                .available(item.getAvailable())
                .request(RequestMapper.toItemRequestDto(item.getRequest()))
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(commentDtoList)
                .build();
    }

    public static Item itemDtoToItem(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(user)
                .available(itemDto.getAvailable())
                .request(RequestMapper.toItemRequest(itemDto.getRequest()))
                .build();
    }

    public static List<ItemDto> listItemToListItemDto(List<Item> items) {
        return items.stream().map(item -> ItemMapper.itemToItemDto(item, null, null, null)).collect(Collectors.toList());
    }

}
