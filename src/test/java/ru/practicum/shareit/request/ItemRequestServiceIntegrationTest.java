package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE , properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {

    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private UserDto savedUser;

    private ItemRequestDto savedItemRequest1;

    @BeforeEach
    void init() {
        UserDto user = UserDto.builder()
                .email("mail@mail.ru")
                .name("Misha")
                .build();

        savedUser = userService.addUser(user);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .requestorId(savedUser.getId())
                .created(LocalDateTime.of(2023, 5, 10, 0, 0, 0, 0))
                .description("Описание запроса")
                .build();

        savedItemRequest1 = itemRequestService.addItemRequest(itemRequestDto, savedUser.getId());

        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .requestorId(savedUser.getId())
                .created(LocalDateTime.of(2023, 3, 10, 0, 0, 0, 0))
                .description("Описание запроса 2")
                .build();

        itemRequestService.addItemRequest(itemRequestDto2, savedUser.getId());

    }

    @Test
    void getItemRequest_whenInvoke_thenReturnedItemRequestDto() {

        ItemRequestDto itemRequest = itemRequestService.getItemRequest(savedUser.getId(), savedItemRequest1.getId());

        assertEquals(itemRequest.getId(), savedItemRequest1.getId());

    }

    @Test
    void getItemRequest_whenUserNotFound_thenReturnedNotFoundException() {

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(99L, savedItemRequest1.getId()));

    }

    @Test
    void getItemRequest_whenItemRequestNotFound_thenReturnedNotFoundException() {

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(savedUser.getId(), 99L));

    }


}
