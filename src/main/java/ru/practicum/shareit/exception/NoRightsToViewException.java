package ru.practicum.shareit.exception;

public class NoRightsToViewException extends RuntimeException{
    public NoRightsToViewException(String message) {
        super(message);
    }
}
