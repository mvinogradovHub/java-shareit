package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final BookingValidator bookingValidator;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingDto addBooking(BookingWithoutObjDto bookingDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not Found"));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Item with ID " + bookingDto.getItemId() + " not found"));
        bookingValidator.checkItemAvailable(item);
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = BookingMapper.bookingDtoToBooking(bookingDto, user, item);
        bookingValidator.checkBookingStartBeforeEnd(booking);
        bookingValidator.checkNotYourOwnItem(userId, booking);
        return BookingMapper.bookingToBookingDto(bookingRepository.save(booking));
    }

    public BookingDto approvedBooking(Long bookingId, Boolean isApproved, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking with ID " + bookingId + " not found"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("The user id {} does not have rights to edit items", userId);
            throw new NotFoundException("User id " + userId + " does not have rights to edit item");
        }
        bookingValidator.checkBookingStatus(isApproved, booking);
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.bookingToBookingDto(bookingRepository.save(booking));
    }

    public BookingDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking with ID " + bookingId + " not found"));
        bookingValidator.checkItemOwnerOrAfter(booking, userId);
        return BookingMapper.bookingToBookingDto(booking);
    }

    public List<BookingDto> getBookerBookings(String state, Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        Pageable page = convertToPageSettings(from, size, "start");
        switch (state) {
            case "ALL":
                return BookingMapper.bookingListToListBookingDto(bookingRepository.findByBookerId(userId, page));
            case "CURRENT":
                return BookingMapper.bookingListToListBookingDto(bookingRepository.findByBookerIdAndStartLessThanAndEndGreaterThan(userId, LocalDateTime.now(), LocalDateTime.now(), page));
            case "FUTURE":
                return BookingMapper.bookingListToListBookingDto(bookingRepository.findByBookerIdAndStartGreaterThan(userId, LocalDateTime.now(), page));
            case "PAST":
                return BookingMapper.bookingListToListBookingDto(bookingRepository.findByBookerIdAndEndLessThan(userId, LocalDateTime.now(), page));
            case "REJECTED":
                return BookingMapper.bookingListToListBookingDto(bookingRepository.getBookerRejectedBooking(userId, page));
            case "WAITING":
                return BookingMapper.bookingListToListBookingDto(bookingRepository.findByBookerIdAndStartGreaterThanEqualAndStatusIs(userId, LocalDateTime.now(), BookingStatus.WAITING, page));
            default:
                throw new BadDataException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public List<BookingDto> getOwnerBookings(String state, Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with ID " + userId + " not found"));
        Pageable page = convertToPageSettings(from, size, "start");
        switch (state) {
            case "ALL":
                return BookingMapper.bookingListToListBookingDto(bookingRepository.findByItemOwnerId(userId, page));
            case "CURRENT":
                return BookingMapper.bookingListToListBookingDto(bookingRepository.findByItemOwnerIdAndStartLessThanAndEndGreaterThan(userId, LocalDateTime.now(), LocalDateTime.now(), page));
            case "FUTURE":
                return BookingMapper.bookingListToListBookingDto(bookingRepository.findByItemOwnerIdAndStartGreaterThan(userId, LocalDateTime.now(), page));
            case "PAST":
                return BookingMapper.bookingListToListBookingDto(bookingRepository.findByItemOwnerIdAndEndLessThan(userId, LocalDateTime.now(), page));
            case "REJECTED":
                return BookingMapper.bookingListToListBookingDto(bookingRepository.getOwnerRejectedBooking(userId, page));
            case "WAITING":
                return BookingMapper.bookingListToListBookingDto(bookingRepository.findByItemOwnerIdAndStartGreaterThanEqualAndStatus(userId, LocalDateTime.now(), BookingStatus.WAITING, page));
            default:
                throw new BadDataException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    public Pageable convertToPageSettings(Integer from, Integer size, String sort) {
        int page = from >= 0 ? Math.round((float) from / size) : -1;
        return PageRequest.of(page, size, Sort.by(sort).descending());
    }
}
