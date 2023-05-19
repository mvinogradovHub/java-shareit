package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .status(booking.getStatus())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .build();
    }

    public static Booking toBooking(BookingWithoutAttachObjDto bookingWithoutAttachObjDto, User user, Item item) {
        return Booking.builder()
                .end(bookingWithoutAttachObjDto.getEnd())
                .start(bookingWithoutAttachObjDto.getStart())
                .item(item)
                .booker(user)
                .status(bookingWithoutAttachObjDto.getStatus())
                .build();
    }

    public static BookingWithoutAttachObjDto toBookingWithoutAttachObjDto(Booking booking) {
        if (booking != null) {
            return BookingWithoutAttachObjDto.builder()
                    .id(booking.getId())
                    .end(booking.getEnd())
                    .start(booking.getStart())
                    .itemId(booking.getItem().getId())
                    .bookerId(booking.getBooker().getId())
                    .status(booking.getStatus())
                    .build();
        }
        return null;
    }

    public static List<BookingDto> toListBookingDto(List<Booking> bookingList) {
        return bookingList.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

}
