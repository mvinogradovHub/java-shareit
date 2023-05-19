package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingWithoutAttachObjDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;


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

    public static Item toItem(ItemDto itemDto, User user) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(user)
                .available(itemDto.getAvailable())
                .request(RequestMapper.toItemRequest(itemDto.getRequest()))
                .build();
    }

    public static ItemWithBookingAndCommentDto toItemWithBookingAndCommentDto(Item item, BookingWithoutAttachObjDto lastBooking, BookingWithoutAttachObjDto nextBooking, List<CommentDto> commentDtoList) {
        return ItemWithBookingAndCommentDto.builder()
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
}
