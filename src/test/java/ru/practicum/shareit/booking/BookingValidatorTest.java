package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BadDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingValidatorTest {

  private User user;
  private Item item;
  private Booking booking;
  private BookingValidator bookingValidator;

  @BeforeEach
  void init() {
    user = User.builder().id(1L).email("mail@mail.ru").name("Misha").build();

    item =
        Item.builder()
            .id(1L)
            .owner(user)
            .name("Ответртка")
            .available(true)
            .description("Описание")
            .build();

    booking =
        Booking.builder()
            .end(LocalDateTime.of(2023, 1, 10, 0, 0, 0, 0))
            .start(LocalDateTime.of(2023, 2, 10, 0, 0, 0, 0))
            .booker(user)
            .item(item)
            .status(BookingStatus.APPROVED)
            .id(1L)
            .build();

    bookingValidator = new BookingValidator();
  }

  @Test
  void checkItemOwnerOrAfter_whenUserNotOwnerOrAfter_thenNotFoundException() {
    assertThrows(
        NotFoundException.class, () -> bookingValidator.checkItemOwnerOrAfter(booking, 99L));
  }

  @Test
  void checkItemOwnerOrAfter_whenUserOwnerOrAfter_thenNotException() {
    assertDoesNotThrow(
        () -> bookingValidator.checkItemOwnerOrAfter(booking, 1L));
  }

  @Test
  void checkItemAvailable_whenItemNotAvailable_thenBadDataException() {
    item.setAvailable(false);
    assertThrows(BadDataException.class, () -> bookingValidator.checkItemAvailable(item));
  }

  @Test
  void checkItemAvailable_whenItemAvailable_thenNotException() {
    assertDoesNotThrow(
        () -> bookingValidator.checkItemAvailable(item));
  }

  @Test
  void checkBookingStartBeforeEnd_whenStartAfterEnd_thenBadDataException() {
    assertThrows(
        BadDataException.class, () -> bookingValidator.checkBookingStartBeforeEnd(booking));
  }

  @Test
  void checkBookingStartBeforeEnd_whenStartBeforeEnd_thenNotException() {
    booking.setEnd(LocalDateTime.of(2023, 5, 10, 0, 0, 0, 0));
    assertDoesNotThrow(
        () -> bookingValidator.checkBookingStartBeforeEnd(booking));
  }

  @Test
  void checkBookingStatus_whenChangeAlsoStatus_WhenBadDataException() {
    assertThrows(BadDataException.class, () -> bookingValidator.checkBookingStatus(true, booking));
  }

  @Test
  void checkNotYourOwnItem_WhenUserOwnerItem_thenNotFoundException() {
    assertThrows(
        NotFoundException.class, () -> bookingValidator.checkNotYourOwnItem(user.getId(), booking));
  }

  @Test
  void checkNotYourOwnItem_WhenUserOwnerItem_thenNotException() {
    assertDoesNotThrow(
        () -> {
          User newUser = User.builder().id(99L).build();
          booking.getItem().setOwner(newUser);
          bookingValidator.checkNotYourOwnItem(newUser.getId(), booking);
        });
  }
}
