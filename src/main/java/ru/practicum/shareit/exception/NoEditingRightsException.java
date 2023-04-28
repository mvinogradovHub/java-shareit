package ru.practicum.shareit.exception;

public class NoEditingRightsException extends RuntimeException {
    public NoEditingRightsException(String message) {
        super(message);
    }
}
