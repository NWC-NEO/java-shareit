package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateParserTest {

    @Test
    void parse_withValidState() {
        State state = StateParser.parse("ALL");
        assertEquals(State.ALL, state);
    }

    @Test
    void parse_withInvalidState_throwsValidationException() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            StateParser.parse("INVALID_STATE_XYZ");
        });
        assertEquals("Неизвестное состояние: INVALID_STATE_XYZ", exception.getMessage());
    }
}
