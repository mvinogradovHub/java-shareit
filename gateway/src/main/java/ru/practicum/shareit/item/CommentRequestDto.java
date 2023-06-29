package ru.practicum.shareit.item;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    @NotBlank
    @NotNull
    @Size(max = 512)
    private String text;

}
