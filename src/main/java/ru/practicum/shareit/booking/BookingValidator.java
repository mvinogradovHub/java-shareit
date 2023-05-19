package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;

@Slf4j
@RequiredArgsConstructor
@Component
public class BookingValidator {

    public void checkItemOwnerOrAfter(Booking booking, Long userId) {
        if (userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId())) {
            return;
        }
        log.warn("Booking can be viewed either by the author or the owner of the item");
        throw new NoRightsToViewException("Booking can be viewed either by the author or the owner of the item");
    }
    public void checkItemAvailable(Item item) {
        if (item.getAvailable()) {
            return;
        }
        log.warn("The item is not available for booking");
        throw new ErrorToCreateException("The item is not available for booking");
    }

    public void checkBookingStartBeforeEnd(Booking booking) {
        if (booking.getEnd().isAfter(booking.getStart())) {
            return;
        }
        log.warn("The start date must be earlier than the end date");
        throw new ErrorToCreateException("The start date must be earlier than the end date");
    }
    public void checkBookingStatus(Boolean isApproved, Booking booking) {
        if (isApproved && booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.warn("Cannot be approved in this status");
            throw new BadStatusException("Cannot be approved in this status");
        }
    }

    public void checkNotYourOwnItem(Long userId, Booking booking) {
        if (!booking.getItem().getId().equals(userId)) {
            return;
        }
        log.warn("You can't book your own Item");
        throw new NotFoundException("You can't book your own Item");

    }



}
