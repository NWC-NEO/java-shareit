package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFoundException() {
        NotFoundException exception = new NotFoundException("Not found");
        Map<String, String> response = errorHandler.handleNotFoundException(exception);
        assertEquals("Not found", response.get("error"));
    }

    @Test
    void handleConflictException() {
        ConflictException exception = new ConflictException("Conflict");
        Map<String, String> response = errorHandler.handleConflictException(exception);
        assertEquals("Conflict", response.get("error"));
    }

    @Test
    void handleBadRequest_withValidationException() {
        ValidationException exception = new ValidationException("Validation error");
        Map<String, String> response = errorHandler.handleBadRequest(exception);
        assertEquals("Validation error", response.get("error"));
    }

    @Test
    void handleBadRequest_withIllegalArgumentException() {
        IllegalArgumentException exception = new IllegalArgumentException("Illegal argument");
        Map<String, String> response = errorHandler.handleBadRequest(exception);
        assertEquals("Illegal argument", response.get("error"));
    }

    @Test
    void handleBadRequest_withMethodArgumentNotValidException() {
        MethodArgumentNotValidException exception = Mockito.mock(MethodArgumentNotValidException.class);
        Map<String, String> response = errorHandler.handleBadRequest(exception);
        assertEquals("Ошибка валидации данных", response.get("error"));
    }
}
