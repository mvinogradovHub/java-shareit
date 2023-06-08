package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private Pageable pageable;

    private User savedOwner;
    private User savedBooker;
    private Item savedItem;

    private Booking nextBooking;

    private Booking lastBooking;

    @BeforeEach
    void init() {
        User owner = User.builder().id(1L).email("mail@mail.ru").name("Misha").build();
        savedOwner = userRepository.save(owner);
        User user = User.builder().id(2L).email("mail@ya.ru").name("Vasia").build();
        savedBooker = userRepository.save(user);

        Item item = Item.builder()
                .name("Отвертка малеНькая")
                .available(true)
                .description("Супер откручивалка")
                .owner(savedOwner)
                .build();

        savedItem = itemRepository.save(item);

        Booking booking1 = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(savedBooker)
                .status(BookingStatus.APPROVED)
                .id(1L)
                .build();

        nextBooking = bookingRepository.save(booking1);

        Booking booking2 = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.of(2023, 1, 10, 0, 0, 0, 0))
                .end(LocalDateTime.of(2023, 2, 10, 0, 0, 0, 0))
                .booker(savedBooker)
                .status(BookingStatus.REJECTED)
                .id(2L)
                .build();

        Booking bookingRejected = bookingRepository.save(booking2);

        Booking booking3 = Booking.builder()
                .item(savedItem)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .booker(savedBooker)
                .status(BookingStatus.APPROVED)
                .id(3L)
                .build();

        lastBooking = bookingRepository.save(booking3);

        pageable = PageRequest.of(0, 10, Sort.by("id").descending());
    }

    @AfterEach
    void deleteAll() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getBookerRejectedBooking_whenInvoke_ReturnedOnlyRejectedBooking() {
        List<Booking> bookingList = bookingRepository.getBookerRejectedBooking(savedBooker.getId(), pageable);
        assertEquals(bookingList.size(), 1);
        assertEquals(bookingList.get(0).getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void getOwnerRejectedBooking_whenInvoke_ReturnedOnlyRejectedBooking() {
        List<Booking> bookingList = bookingRepository.getOwnerRejectedBooking(savedOwner.getId(), pageable);
        assertEquals(bookingList.size(), 1);
        assertEquals(bookingList.get(0).getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void getItemLastBooking_whenInvoke_ReturnedOnlyLastBooking() {
        Optional<Booking> booking = bookingRepository.getItemLastBooking(savedItem.getId(), LocalDateTime.now());
        assertEquals(booking.get().getId(), lastBooking.getId());
    }

    @Test
    void getItemNextBooking_whenInvoke_ReturnedOnlyNextBooking() {
        Optional<Booking> booking = bookingRepository.getItemNextBooking(savedItem.getId(), LocalDateTime.now());
        assertEquals(booking.get().getId(), nextBooking.getId());
    }

    @Test
    void getByBookerAndItemPastApprovedBooking_whenInvoke_ReturnedOnlyPastBooking() {
        Optional<Booking> booking = bookingRepository.getByBookerAndItemPastApprovedBooking(savedBooker.getId(), savedItem.getId(), LocalDateTime.now());
        assertEquals(booking.get().getId(), lastBooking.getId());
    }
}
