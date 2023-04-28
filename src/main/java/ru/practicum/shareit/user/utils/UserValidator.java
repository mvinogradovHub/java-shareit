package ru.practicum.shareit.user.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserMailAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserValidator {
    private final UserRepository userRepository;

    public void checkMailConflict(UserDto userDto) {
        String email = userDto.getEmail();
        if (email == null || userRepository.getUserByEmail(email) == null || userRepository.getUserByEmail(email).getId().equals(userDto.getId())) {
            return;
        }
        log.warn("The email " + userDto.getEmail() + " already exists");
        throw new UserMailAlreadyExistsException("The email " + userDto.getEmail() + " already exists");
    }

    public void checkUserInRepository(Long id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            log.warn("User with ID " + id + " not found");
            throw new NotFoundException("User with ID " + id + " not found");
        }
    }

}
