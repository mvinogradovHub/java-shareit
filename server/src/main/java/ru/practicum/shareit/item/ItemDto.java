package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingWithoutObjDto;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private Long requestId;
    private BookingWithoutObjDto nextBooking;
    private BookingWithoutObjDto lastBooking;
    private List<CommentDto> comments;
}
