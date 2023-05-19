package ru.practicum.shareit.exception;

public class UserMailAlreadyExistsException extends RuntimeException {
    public UserMailAlreadyExistsException(String message) {
        super(message);
    }
}
