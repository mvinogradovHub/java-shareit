package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public UserDto addUser(UserDto userDto) {
    return UserMapper.userToUserDto(userRepository.save(UserMapper.userDtoToUser(userDto)));
  }

  public UserDto updateUser(UserDto userDto, Long id) {
    userDto.setId(id);
    User userInStorage =
        userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    userInStorage.setId(id);
    if (userDto.getName() != null) {
      userInStorage.setName(userDto.getName());
    }
    if (userDto.getEmail() != null) {
      userInStorage.setEmail(userDto.getEmail());
    }
    return UserMapper.userToUserDto(userRepository.save(userInStorage));
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  public List<UserDto> getUsers() {
    return userRepository.findAll().stream()
        .map(UserMapper::userToUserDto)
        .collect(Collectors.toList());
  }

  public UserDto getUserById(Long id) {
    return UserMapper.userToUserDto(
        userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found")));
  }
}
