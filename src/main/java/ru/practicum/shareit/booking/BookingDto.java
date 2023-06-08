package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@Builder
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    @NotNull
    private ItemDto item;
    @NotNull
    private UserDto booker;
    private BookingStatus status;
}
