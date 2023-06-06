package ru.practicum.shareit.user;

public class UserMapper {
  public static UserDto userToUserDto(User user) {
    return UserDto.builder().id(user.getId()).email(user.getEmail()).name(user.getName()).build();
  }

  public static User userDtoToUser(UserDto userDto) {
    return User.builder()
        .id(userDto.getId())
        .email(userDto.getEmail())
        .name(userDto.getName())
        .build();
  }
}
