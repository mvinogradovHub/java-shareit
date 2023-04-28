package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.utils.UserValidator;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;

    public UserDto addUser(UserDto userDto) {
        userValidator.checkMailConflict(userDto);
        return UserMapper.toUserDto(userRepository.addUser(UserMapper.toUser(userDto)));
    }

    public UserDto updateUser(UserDto userDto, Long id) {
        userDto.setId(id);
        userValidator.checkUserInRepository(id);
        userValidator.checkMailConflict(userDto);
        User userInStorage = userRepository.getUserById(id);
        userInStorage.setId(id);
        if (userDto.getName() != null) {
            userInStorage.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userInStorage.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.updateUser(userInStorage));

    }

    public void deleteUser(Long id) {
        userValidator.checkUserInRepository(id);
        userRepository.deleteUser(id);
    }

    public List<UserDto> getUsers() {
        return userRepository.getUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());

    }

    public UserDto getUserById(Long id) {
        userValidator.checkUserInRepository(id);
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }


}
