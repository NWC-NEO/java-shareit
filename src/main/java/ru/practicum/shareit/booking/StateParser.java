package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.ValidationException;

public class StateParser {

    public static State parse(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Неизвестное состояние: " + state);
        }
    }
}
