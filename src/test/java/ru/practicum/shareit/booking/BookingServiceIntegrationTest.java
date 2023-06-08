package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {

    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private UserDto savedOwner;
    private UserDto savedBooker;
    private ItemDto savedItem;
    private BookingDto nextBooking;
    private BookingDto lastBooking;
    private BookingDto bookingRejected;

    @BeforeEach
    void addBookings() {
        UserDto owner = UserDto.builder().id(1L).email("mail@mail.ru").name("Misha").build();
        savedOwner = userService.addUser(owner);
        UserDto user = UserDto.builder().id(2L).email("mail@ya.ru").name("Vasia").build();
        savedBooker = userService.addUser(user);

        ItemDto item = ItemDto.builder()
                .name("Отвертка малеНькая")
                .available(true)
                .description("Супер откручивалка")
                .owner(savedOwner)
                .build();

        savedItem = itemService.addItem(item, savedOwner.getId());

        BookingWithoutObjDto booking1 = BookingWithoutObjDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .bookerId(savedBooker.getId())
                .build();

        nextBooking = bookingService.addBooking(booking1, savedBooker.getId());

        BookingWithoutObjDto booking2 = BookingWithoutObjDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.of(2023, 1, 10, 0, 0, 0, 0))
                .end(LocalDateTime.of(2023, 2, 10, 0, 0, 0, 0))
                .bookerId(savedBooker.getId())
                .status(BookingStatus.REJECTED)
                .id(2L)
                .build();

        bookingRejected = bookingService.addBooking(booking2, savedBooker.getId());
        bookingService.approvedBooking(bookingRejected.getId(), false, savedOwner.getId());

        BookingWithoutObjDto booking3 = BookingWithoutObjDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().plusDays(1))
                .bookerId(savedBooker.getId())
                .build();

        lastBooking = bookingService.addBooking(booking3, savedBooker.getId());
        bookingService.approvedBooking(lastBooking.getId(), true, savedOwner.getId());


    }

    @Test
    void getBookerBookings_whenInvokeWithStateREJECTED_ReturnedOnlyRejectedBooking() {
        List<BookingDto> bookingList = bookingService.getBookerBookings("REJECTED", savedBooker.getId(), 0, 10);
        assertEquals(bookingList.size(), 1);
        assertEquals(bookingList.get(0).getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void getBookerBookings_whenInvokeWithStateALL_ReturnedALLBooking() {
        List<BookingDto> bookingList = bookingService.getBookerBookings("ALL", savedBooker.getId(), 0, 10);
        assertEquals(bookingList.size(), 3);
    }

    @Test
    void getBookerBookings_whenInvokeWithStateCURRENT_ReturnedCURRENTBooking() {
        List<BookingDto> bookingList = bookingService.getBookerBookings("CURRENT", savedBooker.getId(), 0, 10);
        assertEquals(bookingList.size(), 1);
        assertEquals(lastBooking.getId(), bookingList.get(0).getId());
    }

    @Test
    void getBookerBookings_whenInvokeWithStateFUTURE_ReturnedFUTUREBooking() {
        List<BookingDto> bookingList = bookingService.getBookerBookings("FUTURE", savedBooker.getId(), 0, 10);
        assertEquals(bookingList.size(), 1);
        assertEquals(nextBooking.getId(), bookingList.get(0).getId());
    }

    @Test
    void getBookerBookings_whenInvokeWithStatePAST_ReturnedOnlyPastBooking() {
        List<BookingDto> bookingList = bookingService.getBookerBookings("PAST", savedBooker.getId(), 0, 10);
        assertEquals(bookingList.size(), 1);
        assertEquals(bookingRejected.getId(), bookingList.get(0).getId());
    }

    @Test
    void getBookerBookings_whenInvokeWithStateWAITING_ReturnedOnlyWaitingBooking() {
        List<BookingDto> bookingList = bookingService.getBookerBookings("WAITING", savedBooker.getId(), 0, 10);
        assertEquals(bookingList.size(), 1);
        assertEquals(nextBooking.getId(), bookingList.get(0).getId());
    }

    @Test
    void getOwnerBookings_whenInvokeWithStateREJECTED_ReturnedOnlyRejectedBooking() {
        List<BookingDto> bookingList = bookingService.getOwnerBookings("REJECTED", savedOwner.getId(), 0, 10);
        assertEquals(bookingList.size(), 1);
        assertEquals(bookingList.get(0).getStatus(), BookingStatus.REJECTED);
    }


    @Test
    void approvedBooking_whenApprovedBooking_thenReturnApprovedBookingDto() {
        BookingDto approvedBooking = bookingService.approvedBooking(nextBooking.getId(), true, savedOwner.getId());
        assertEquals(approvedBooking.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void addBooking_whenAddBooking_thenStatusWaiting() {
        BookingWithoutObjDto newBooking = BookingWithoutObjDto.builder()
                .itemId(savedItem.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .bookerId(savedBooker.getId())
                .build();
        BookingDto savedBooking = bookingService.addBooking(newBooking, savedBooker.getId());
        assertEquals(savedBooking.getStatus(), BookingStatus.WAITING);
    }


}
