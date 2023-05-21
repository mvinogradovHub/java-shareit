package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadDataException;
import ru.practicum.shareit.exception.NotFoundException;
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

    public BookingDto addBooking(BookingWithoutObjDto bookingWithoutObjDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not Found"));
        Item item = itemRepository.findById(bookingWithoutObjDto.getItemId()).orElseThrow(() -> new NotFoundException("Item with ID " + bookingWithoutObjDto.getItemId() + " not found"));
        bookingValidator.checkItemAvailable(item);
        bookingWithoutObjDto.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.BookingDtoToBooking(bookingWithoutObjDto, user, item);
        bookingValidator.checkBookingStartBeforeEnd(booking);
        bookingValidator.checkNotYourOwnItem(userId,booking);
        return BookingMapper.BookingToBookingDto(bookingRepository.save(booking));
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

        return BookingMapper.BookingToBookingDto(bookingRepository.save(booking));
    }

    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking with ID " + bookingId + " not found"));
        bookingValidator.checkItemOwnerOrAfter(booking, userId);
        return BookingMapper.BookingToBookingDto(booking);
    }

    public List<BookingDto> getBookerBookings(String state, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        switch (state) {
            case "ALL":
                return BookingMapper.BookingListToListBookingDto(bookingRepository.findByBookerIdOrderByStartDesc(userId));
            case "CURRENT":
                return BookingMapper.BookingListToListBookingDto(bookingRepository.findByBookerIdAndStartLessThanAndEndGreaterThanOrderByStartDesc(userId, LocalDateTime.now(),LocalDateTime.now()));
            case "FUTURE":
                return BookingMapper.BookingListToListBookingDto(bookingRepository.findByBookerIdAndStartGreaterThanOrderByStartDesc(userId, LocalDateTime.now()));
            case "PAST":
                return BookingMapper.BookingListToListBookingDto(bookingRepository.findByBookerIdAndEndLessThanOrderByStartDesc(userId, LocalDateTime.now()));
            case "REJECTED":
                return BookingMapper.BookingListToListBookingDto(bookingRepository.getBookerRejectedBooking(userId));
            case "WAITING":
                return BookingMapper.BookingListToListBookingDto(bookingRepository.findByBookerIdAndStartGreaterThanEqualAndStatusIsOrderByStartDesc(userId, LocalDateTime.now(),BookingStatus.WAITING));
            default:
                throw new BadDataException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public List<BookingDto> getOwnerBookings(String state, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        switch (state) {
            case "ALL":
                return BookingMapper.BookingListToListBookingDto(bookingRepository.getOwnerBooking(userId));
            case "CURRENT":
                return BookingMapper.BookingListToListBookingDto(bookingRepository.getOwnerCurrentBooking(userId, LocalDateTime.now()));
            case "FUTURE":
                return BookingMapper.BookingListToListBookingDto(bookingRepository.getOwnerFutureBooking(userId, LocalDateTime.now()));
            case "PAST":
                return BookingMapper.BookingListToListBookingDto(bookingRepository.getOwnerPastBooking(userId, LocalDateTime.now()));
            case "REJECTED":
                return BookingMapper.BookingListToListBookingDto(bookingRepository.getOwnerRejectedBooking(userId));
            case "WAITING":
                return BookingMapper.BookingListToListBookingDto(bookingRepository.getOwnerWaitingBooking(userId, LocalDateTime.now()));
            default:
                throw new BadDataException("Unknown state: UNSUPPORTED_STATUS");
        }
    }


}
