package ru.practicum.shareit.item;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotNull
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}
