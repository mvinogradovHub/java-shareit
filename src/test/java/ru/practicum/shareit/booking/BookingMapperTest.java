package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {
    private User user;
    private Item item;

    private Booking booking;

    private BookingWithoutObjDto bookingDto;

    @BeforeEach
    void init() {

        ItemRequest itemRequest = ItemRequest.builder()
                .id(5L)
                .build();

        user = User.builder()
                .id(2L)
                .build();

        item = Item.builder()
                .id(1L)
                .owner(user)
                .name("Ответртка")
                .available(true)
                .description("Описание")
                .request(itemRequest)
                .build();

        booking = Booking.builder()
                .item(item)
                .end(LocalDateTime.of(2023, 1, 10, 0, 0, 0, 0))
                .start(LocalDateTime.of(2023, 2, 10, 0, 0, 0, 0))
                .booker(user)
                .status(BookingStatus.APPROVED)
                .id(1L)
                .build();

        bookingDto = BookingWithoutObjDto.builder()
                .itemId(3L)
                .end(LocalDateTime.of(2023, 1, 10, 0, 0, 0, 0))
                .start(LocalDateTime.of(2023, 2, 10, 0, 0, 0, 0))
                .bookerId(5L)
                .status(BookingStatus.APPROVED)
                .id(1L)
                .build();
    }

    @Test
    void bookingToBookingDto_whenSendBooking_thenReturnBookingDto() {
        BookingDto newBookingDto = BookingMapper.bookingToBookingDto(booking);

        assertEquals(newBookingDto.getId(), booking.getId());
        assertEquals(newBookingDto.getEnd(), booking.getEnd());
        assertEquals(newBookingDto.getStart(), booking.getStart());
        assertEquals(newBookingDto.getBooker().getId(), booking.getBooker().getId());
        assertEquals(newBookingDto.getItem().getId(), booking.getItem().getId());
        assertEquals(newBookingDto.getStatus(), booking.getStatus());
    }

    @Test
    void bookingDtoToBooking_whenSendBookingDto_thenReturnBooking() {
        Booking newBooking = BookingMapper.bookingDtoToBooking(bookingDto, user, item);

        assertEquals(newBooking.getId(), bookingDto.getId());
        assertEquals(newBooking.getEnd(), bookingDto.getEnd());
        assertEquals(newBooking.getStart(), bookingDto.getStart());
        assertEquals(newBooking.getBooker().getId(), user.getId());
        assertEquals(newBooking.getItem().getId(), item.getId());
        assertEquals(newBooking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void bookingToBookingWithoutObjDto_whenSendNull_thenReturnNull() {
        assertNull(BookingMapper.bookingToBookingWithoutObjDto(null));
    }

    @Test
    void bookingToBookingWithoutObjDto_whenSendBooking_thenReturnBookingWithoutObjDto() {
        BookingWithoutObjDto newBooking = BookingMapper.bookingToBookingWithoutObjDto(booking);

        assertEquals(newBooking.getId(), booking.getId());
        assertEquals(newBooking.getEnd(), booking.getEnd());
        assertEquals(newBooking.getStart(), booking.getStart());
        assertEquals(newBooking.getBookerId(), booking.getBooker().getId());
        assertEquals(newBooking.getItemId(), booking.getItem().getId());
        assertEquals(newBooking.getStatus(), booking.getStatus());
    }

    @Test
    void bookingListToListBookingDto_whenSendListBooking_thenReturnListBookingDto() {
        List<Booking> bookings = List.of(booking);

        List<BookingDto> bookingDtos = BookingMapper.bookingListToListBookingDto(bookings);
        assertEquals(bookings.get(0).getId(), bookingDtos.get(0).getId());
    }
}
