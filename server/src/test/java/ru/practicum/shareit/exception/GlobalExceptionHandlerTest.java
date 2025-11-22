package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleMethodArgumentTypeMismatch_ShouldReturnBadRequest() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("bookingId");

        ResponseEntity<Map<String, String>> response = errorHandler.handleMethodArgumentTypeMismatch(ex);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("error"));
        assertTrue(response.getBody().get("error").contains("Invalid parameter"));
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        Exception ex = new Exception("Generic error");

        ResponseEntity<Map<String, String>> response = errorHandler.handleException(ex);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("Internal server error", response.getBody().get("error"));
    }
}