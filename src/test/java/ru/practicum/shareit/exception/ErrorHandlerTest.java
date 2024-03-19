package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {
    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void errorValidation() {
        ValidationException exception = new ValidationException("Ошибка валидации");
        ErrorResponse response = errorHandler.errorValidation(exception);
        assertEquals("Ошибка валидации", response.getError());
    }

    @Test
    void incorrectRequest() {
        NotFoundException exception = new NotFoundException("Не нашел");
        ErrorResponse response = errorHandler.incorrectRequest(exception);
        assertEquals("Не нашел", response.getError());
    }

    @Test
    void otherException() {
        RuntimeException exception = new RuntimeException("Всё пропало!");
        ErrorResponse response = errorHandler.otherException(exception);
        assertEquals("Всё пропало!", response.getError());
    }

    @Test
    void conflictException() {
        ConflictException exception = new ConflictException("Всё решим.");
        ErrorResponse response = errorHandler.conflictException(exception);
        assertEquals("Всё решим.", response.getError());
    }
}