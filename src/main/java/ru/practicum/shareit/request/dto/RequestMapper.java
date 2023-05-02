package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;

/*методы и сами классы ItemRequest и ItemRequestDto доработаю в последующих спринтах т.к. в этом не требовалось,
но мне нужен RequestMapper чтобы использовать ItemRequestDto при сборке Item*/
public class RequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        if (itemRequestDto == null) {
            return null;
        }
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .build();
    }
}
