package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

  @Test
  void userToUserDto_whenSendUser_thenReturnLikeUserDto() {
    User user = User.builder().id(1L).email("mail@mail.ru").name("Misha").build();

    UserDto userDto = UserMapper.userToUserDto(user);

    assertEquals(user.getId(), userDto.getId());
    assertEquals(user.getName(), userDto.getName());
    assertEquals(user.getEmail(), userDto.getEmail());
  }

  @Test
  void userDtoToUser_whenSendUserDto_thenReturnLikeUser() {
    UserDto userDto = UserDto.builder().id(1L).email("mail@mail.ru").name("Misha").build();

    User user = UserMapper.userDtoToUser(userDto);

    assertEquals(user.getId(), userDto.getId());
    assertEquals(user.getName(), userDto.getName());
    assertEquals(user.getEmail(), userDto.getEmail());
  }
}
