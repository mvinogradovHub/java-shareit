package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.BadDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingValidator bookingValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @Captor
    private ArgumentCaptor<Pageable> pageArgumentCaptor;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Item item;
    private BookingWithoutObjDto bookingDto;
    private Booking booking;

    @BeforeEach
    void init() {
        user = User.builder().id(1L).email("mail@mail.ru").name("Misha").build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(5L)
                .requestor(user)
                .created(LocalDateTime.of(2023, 3, 10, 0, 0, 0, 0))
                .items(new ArrayList<>())
                .description("Описание запроса")
                .build();

        item = Item.builder()
                .id(1L)
                .owner(user)
                .name("Ответртка")
                .available(true)
                .description("Описание")
                .request(itemRequest)
                .build();

        this.bookingDto = BookingWithoutObjDto.builder()
                .itemId(item.getId())
                .end(LocalDateTime.of(2023, 1, 10, 0, 0, 0, 0))
                .start(LocalDateTime.of(2023, 2, 10, 0, 0, 0, 0))
                .bookerId(user.getId())
                .status(BookingStatus.APPROVED)
                .id(1L)
                .build();

        booking = Booking.builder()
                .item(item)
                .end(LocalDateTime.of(2023, 1, 10, 0, 0, 0, 0))
                .start(LocalDateTime.of(2023, 2, 10, 0, 0, 0, 0))
                .booker(user)
                .status(BookingStatus.APPROVED)
                .id(1L)
                .build();
    }

    @Test
    void addBooking_whenUserNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingDto, user.getId()));
        verify(bookingRepository, never()).save(Mockito.any());
    }

    @Test
    void addBooking_whenItemRequestNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(bookingDto, user.getId()));
        verify(bookingRepository, never()).save(Mockito.any());
    }

    @Test
    void addBooking_whenInvoke_thenValidate() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(Mockito.any())).thenReturn(booking);

        bookingService.addBooking(bookingDto, user.getId());

        verify(bookingValidator, Mockito.atLeast(1)).checkItemAvailable(Mockito.any());
        verify(bookingValidator, Mockito.atLeast(1)).checkBookingStartBeforeEnd(Mockito.any());
        verify(bookingValidator, Mockito.atLeast(1)).checkNotYourOwnItem(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void approvedBooking_whenUserNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approvedBooking(1L, true, 1L));
        verify(bookingRepository, never()).save(Mockito.any());
    }

    @Test
    void approvedBooking_whenBookingNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approvedBooking(1L, true, 1L));
    }

    @Test
    void approvedBooking_whenUserNotOwner_thenNotFoundException() {
        User newUser = User.builder().id(99L).build();
        booking.getItem().setOwner(newUser);
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.approvedBooking(1L, true, 1L));
    }

    @Test
    void approvedBooking_whenApproved_thenStatusChanged() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto actualBookingDto = bookingService.approvedBooking(1L, false, 1L);

        assertEquals(actualBookingDto.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void getBooking_whenInvoke_thenValidate() {
        when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));

        bookingService.getBooking(booking.getId(), user.getId());

        verify(bookingValidator, Mockito.atLeast(1)).checkItemOwnerOrAfter(Mockito.any(), Mockito.any());
    }

    @Test
    void getOwnerBookings_whenUserNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getOwnerBookings("ALL", 1L, 0, 10));
    }

    @Test
    void getOwnerBookings_whenStateALL_thenInvokeGetOwnerBookings() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        bookingService.getOwnerBookings("ALL", user.getId(), 0, 10);

        verify(bookingRepository, Mockito.atLeast(1)).findByItemOwnerId(user.getId(), PageRequest.of(0, 10, Sort.by("start").descending()));
    }

    @Test
    void getOwnerBookings_whenStateCURRENT_thenInvokeGetOwnerCurrentBooking() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        bookingService.getOwnerBookings("CURRENT", user.getId(), 0, 10);

        verify(bookingRepository, Mockito.atLeast(1)).findByItemOwnerIdAndStartLessThanAndEndGreaterThan(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void getOwnerBookings_whenStateFUTURE_thenInvokeGetOwnerFutureBooking() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        bookingService.getOwnerBookings("FUTURE", user.getId(), 0, 10);

        verify(bookingRepository, Mockito.atLeast(1)).findByItemOwnerIdAndStartGreaterThan(Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    void getOwnerBookings_whenStatePAST_thenInvokeGetOwnerPastBooking() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        bookingService.getOwnerBookings("PAST", user.getId(), 0, 10);

        verify(bookingRepository, Mockito.atLeast(1)).findByItemOwnerIdAndEndLessThan(Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    void getOwnerBookings_whenStateREJECTED_thenInvokeGetOwnerRejectedBooking() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        bookingService.getOwnerBookings("REJECTED", user.getId(), 0, 10);

        verify(bookingRepository, Mockito.atLeast(1)).getOwnerRejectedBooking(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getOwnerBookings_whenStateWAITING_thenInvokeGetOwnerWaitingBooking() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        bookingService.getOwnerBookings("WAITING", user.getId(), 0, 10);

        verify(bookingRepository, Mockito.atLeast(1)).findByItemOwnerIdAndStartGreaterThanEqualAndStatus(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void getOwnerBookings_whenStateUnknown_thenBadDataException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        assertThrows(BadDataException.class, () -> bookingService.getOwnerBookings("Unknown", 1L, 0, 10));
    }

    @Test
    void getBookerBookings_whenUserNotFound_thenNotFoundException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookerBookings("ALL", 1L, 0, 10));
    }

    @Test
    void getBookerBookings_whenStateALL_thenInvokeFindByBookerId() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        bookingService.getBookerBookings("ALL", user.getId(), 0, 10);

        verify(bookingRepository, Mockito.atLeast(1)).findByBookerId(user.getId(), PageRequest.of(0, 10, Sort.by("start").descending()));
    }

    @Test
    void getBookerBookings_whenStateCURRENT_thenInvokeFindByBookerIdAndStartLessThanAndEndGreaterThan() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        bookingService.getBookerBookings("CURRENT", user.getId(), 0, 10);

        verify(bookingRepository, Mockito.atLeast(1)).findByBookerIdAndStartLessThanAndEndGreaterThan(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void getBookerBookings_whenStateFUTURE_thenInvokeFindByBookerIdAndStartGreaterThan() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        bookingService.getBookerBookings("FUTURE", user.getId(), 0, 10);

        verify(bookingRepository, Mockito.atLeast(1)).findByBookerIdAndStartGreaterThan(Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    void getBookerBookings_whenStatePAST_thenInvokeFindByBookerIdAndEndLessThan() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        bookingService.getBookerBookings("PAST", user.getId(), 0, 10);

        verify(bookingRepository, Mockito.atLeast(1)).findByBookerIdAndEndLessThan(Mockito.anyLong(), Mockito.any(), Mockito.any());
    }

    @Test
    void getBookerBookings_whenStateREJECTED_thenInvokeGetBookerRejectedBooking() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        bookingService.getBookerBookings("REJECTED", user.getId(), 0, 10);

        verify(bookingRepository, Mockito.atLeast(1)).getBookerRejectedBooking(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void getBookerBookings_whenStateWAITING_thenInvokeFindByBookerIdAndStartGreaterThanEqualAndStatusIs() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        bookingService.getBookerBookings("WAITING", user.getId(), 0, 10);

        verify(bookingRepository, Mockito.atLeast(1)).findByBookerIdAndStartGreaterThanEqualAndStatusIs(Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void getBookerBookings_whenStateUnknown_thenBadDataException() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        assertThrows(BadDataException.class, () -> bookingService.getBookerBookings("Unknown", 1L, 0, 10));
    }

    @Test
    void convertToPageSettings_whenInvokeStart13_thenInvokeWithPage3() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        bookingService.getBookerBookings("ALL", user.getId(), 13, 5);

        verify(bookingRepository).findByBookerId(Mockito.anyLong(), pageArgumentCaptor.capture());
        Pageable savedPage = pageArgumentCaptor.getValue();

        assertEquals(3, savedPage.getPageNumber());
    }
}
