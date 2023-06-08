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

    public static BookingDto bookingToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .item(ItemMapper.itemToItemDto(booking.getItem(), null, null, null))
                .status(booking.getStatus())
                .booker(UserMapper.userToUserDto(booking.getBooker()))
                .build();
    }

    public static Booking bookingDtoToBooking(BookingWithoutObjDto bookingDto, User user, Item item) {
        return Booking.builder()
                .id(bookingDto.getId())
                .end(bookingDto.getEnd())
                .start(bookingDto.getStart())
                .item(item)
                .booker(user)
                .status(bookingDto.getStatus())
                .build();
    }

    public static BookingWithoutObjDto bookingToBookingWithoutObjDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingWithoutObjDto.builder()
                .id(booking.getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public static List<BookingDto> bookingListToListBookingDto(List<Booking> bookingList) {
        return bookingList.stream()
                .map(BookingMapper::bookingToBookingDto)
                .collect(Collectors.toList());
    }
}
