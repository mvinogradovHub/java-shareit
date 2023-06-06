package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/** TODO Sprint add-item-requests. */
@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {
  private Long id;
  @NotNull @NotBlank private String description;
  private Long requestorId;
  private LocalDateTime created;
  private List<ItemDto> items;
}
