package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
@AllArgsConstructor
public class BookingWithoutObjDto {
    private Long id;
    @NotNull
    @FutureOrPresent(message = "The start date cannot be in the past")
    private LocalDateTime start;
    @NotNull
    @FutureOrPresent(message = "The end date cannot be in the past")
    private LocalDateTime end;
    @NotNull
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;

}
