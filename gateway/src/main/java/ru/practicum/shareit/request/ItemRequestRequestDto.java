package ru.practicum.shareit.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestRequestDto {
    @NotNull
    @NotBlank
    private String description;
}
