package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/** TODO Sprint add-bookings. */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
  private final BookingService bookingService;

  @PostMapping
  public BookingDto addBooking(
      @Valid @RequestBody BookingWithoutObjDto bookingWithoutObjDto,
      @RequestHeader("X-Sharer-User-Id") Long userId) {
    log.info(
        "Received request to POST /bookings with RequestHeader X-Sharer-User-Id = {} and body: {}",
        userId,
        bookingWithoutObjDto);
    return bookingService.addBooking(bookingWithoutObjDto, userId);
  }

  @PatchMapping("/{bookingId}")
  public BookingDto approvedBooking(
      @RequestHeader("X-Sharer-User-Id") Long userId,
      @PathVariable Long bookingId,
      @RequestParam Boolean approved) {
    log.info(
        "Received request to PATCH /bookings/{} with RequestHeader X-Sharer-User-Id = {} and approved: {}",
        bookingId,
        userId,
        approved);
    return bookingService.approvedBooking(bookingId, approved, userId);
  }

  @GetMapping("/{bookingId}")
  public BookingDto getBooking(
      @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
    log.info(
        "Received request to GET /bookings/{} with RequestHeader X-Sharer-User-Id = {}",
        bookingId,
        userId);
    return bookingService.getBooking(bookingId, userId);
  }

  @GetMapping
  public List<BookingDto> getBookerBookings(
      @RequestHeader("X-Sharer-User-Id") Long userId,
      @RequestParam(defaultValue = "ALL") String state,
      @RequestParam(defaultValue = "0") Integer from,
      @RequestParam(defaultValue = "10") Integer size) {
    log.info(
        "Received request to GET /bookings?state={} with RequestHeader X-Sharer-User-Id = {}",
        state,
        userId);
    return bookingService.getBookerBookings(state, userId, from, size);
  }

  @GetMapping("/owner")
  public List<BookingDto> getOwnerBookings(
      @RequestHeader("X-Sharer-User-Id") Long userId,
      @RequestParam(defaultValue = "ALL") String state,
      @RequestParam(defaultValue = "0") Integer from,
      @RequestParam(defaultValue = "10") Integer size) {
    log.info(
        "Received request to GET /bookings/owner?state={} with RequestHeader X-Sharer-User-Id = {}",
        state,
        userId);
    return bookingService.getOwnerBookings(state, userId, from, size);
  }
}
