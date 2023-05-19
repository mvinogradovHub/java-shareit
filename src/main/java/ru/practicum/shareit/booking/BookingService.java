package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemValidator itemValidator;
    private final BookingValidator bookingValidator;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingDto addBooking(BookingWithoutAttachObjDto bookingWithoutAttachObjDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not Found"));
        Item item = itemRepository.findById(bookingWithoutAttachObjDto.getItemId()).orElseThrow(() -> new NotFoundException("Item with ID " + bookingWithoutAttachObjDto.getItemId() + " not found"));
        bookingValidator.checkItemAvailable(item);
        bookingWithoutAttachObjDto.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.toBooking(bookingWithoutAttachObjDto, user, item);
        bookingValidator.checkBookingStartBeforeEnd(booking);
        bookingValidator.checkNotYourOwnItem(userId,booking);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto approvedBooking(Long bookingId, Boolean isApproved, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking with ID " + bookingId + " not found"));
        itemValidator.checkItemOwner(userId, booking.getItem());
        bookingValidator.checkBookingStatus(isApproved,booking);
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking with ID " + bookingId + " not found"));
        bookingValidator.checkItemOwnerOrAfter(booking, userId);
        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getBookerBookings(String state, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        switch (state) {
            case "ALL":
                return BookingMapper.toListBookingDto(bookingRepository.findByBookerIdOrderByStartDesc(userId));
            case "CURRENT":
                return BookingMapper.toListBookingDto(bookingRepository.getBookerCurrentBooking(userId, LocalDateTime.now()));
            case "FUTURE":
                return BookingMapper.toListBookingDto(bookingRepository.getBookerFutureBooking(userId, LocalDateTime.now()));
            case "PAST":
                return BookingMapper.toListBookingDto(bookingRepository.getBookerPastBooking(userId, LocalDateTime.now()));
            case "REJECTED":
                return BookingMapper.toListBookingDto(bookingRepository.getBookerRejectedBooking(userId));
            case "WAITING":
                return BookingMapper.toListBookingDto(bookingRepository.getBookerWaitingBooking(userId, LocalDateTime.now()));
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public List<BookingDto> getOwnerBookings(String state, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        switch (state) {
            case "ALL":
                return BookingMapper.toListBookingDto(bookingRepository.getOwnerBooking(userId));
            case "CURRENT":
                return BookingMapper.toListBookingDto(bookingRepository.getOwnerCurrentBooking(userId, LocalDateTime.now()));
            case "FUTURE":
                return BookingMapper.toListBookingDto(bookingRepository.getOwnerFutureBooking(userId, LocalDateTime.now()));
            case "PAST":
                return BookingMapper.toListBookingDto(bookingRepository.getOwnerPastBooking(userId, LocalDateTime.now()));
            case "REJECTED":
                return BookingMapper.toListBookingDto(bookingRepository.getOwnerRejectedBooking(userId));
            case "WAITING":
                return BookingMapper.toListBookingDto(bookingRepository.getOwnerWaitingBooking(userId, LocalDateTime.now()));
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
    }


}
