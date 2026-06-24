package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateParserTest {

    @Test
    void parseValidState() {
        assertEquals(
                State.ALL,
                StateParser.parse("ALL")
        );
    }

    @Test
    void parseInvalidState() {
        assertThrows(
                ValidationException.class,
                () -> StateParser.parse("INVALID")
        );
    }
}
