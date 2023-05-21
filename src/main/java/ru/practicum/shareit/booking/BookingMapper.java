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

    public static BookingDto BookingToBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .item(ItemMapper.ItemToItemDto(booking.getItem(),null,null,null))
                .status(booking.getStatus())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .build();
    }

    public static Booking BookingDtoToBooking(BookingWithoutObjDto bookingWithoutObjDto, User user, Item item) {
        return Booking.builder()
                .end(bookingWithoutObjDto.getEnd())
                .start(bookingWithoutObjDto.getStart())
                .item(item)
                .booker(user)
                .status(bookingWithoutObjDto.getStatus())
                .build();
    }

    public static BookingWithoutObjDto BookingToBookingWithoutObjDto(Booking booking) {
        if (booking != null) {
            return BookingWithoutObjDto.builder()
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

    public static List<BookingDto> BookingListToListBookingDto(List<Booking> bookingList) {
        return bookingList.stream().map(BookingMapper::BookingToBookingDto).collect(Collectors.toList());
    }

}
