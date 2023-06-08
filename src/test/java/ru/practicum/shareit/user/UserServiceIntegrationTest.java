package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    private final UserService userService;
    private UserDto savedUser;

    private UserDto updateUser;

    @BeforeEach
    void init() {
        UserDto user = UserDto.builder()
                .email("mail@mail.ru")
                .name("Misha")
                .build();

        updateUser = UserDto.builder()
                .id(77L)
                .email("update@mail.ru")
                .name("Vasa")
                .build();

        savedUser = userService.addUser(user);
    }

    @Test
    void updateUser_whenUserNotFound_thenReturnedNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.updateUser(updateUser, 99L));
    }

    @Test
    void updateUser_whenUpdate_thenChangeOnlyNameAndEmail() {
        UserDto userDto = userService.updateUser(updateUser, savedUser.getId());

        assertEquals(userDto.getId(), savedUser.getId());
        assertEquals(userDto.getName(), updateUser.getName());
        assertEquals(userDto.getEmail(), updateUser.getEmail());
    }

}
