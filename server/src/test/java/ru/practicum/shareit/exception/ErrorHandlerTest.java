package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleUserNotFoundException_ShouldReturnNotFound() {
        UserNotFoundException ex = new UserNotFoundException("User not found");

        ResponseEntity<Map<String, String>> response = errorHandler.handleUserNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
        assertEquals("User not found", response.getBody().get("error"));
    }

    @Test
    void handleItemNotFoundException_ShouldReturnNotFound() {
        ItemNotFoundException ex = new ItemNotFoundException("Item not found");

        ResponseEntity<Map<String, String>> response = errorHandler.handleItemNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    void handleBookingNotFoundException_ShouldReturnNotFound() {
        BookingNotFoundException ex = new BookingNotFoundException("Booking not found");

        ResponseEntity<Map<String, String>> response = errorHandler.handleBookingNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    void handleItemRequestNotFoundException_ShouldReturnNotFound() {
        ItemRequestNotFoundException ex = new ItemRequestNotFoundException("Request not found");

        ResponseEntity<Map<String, String>> response = errorHandler.handleItemRequestNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    void handleEmailAlreadyExistsException_ShouldReturnConflict() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("Email exists");

        ResponseEntity<Map<String, String>> response = errorHandler.handleEmailAlreadyExistsException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        ValidationException ex = new ValidationException("Validation error");

        ResponseEntity<Map<String, String>> response = errorHandler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    void handleItemAccessDeniedException_ShouldReturnForbidden() {
        ItemAccessDeniedException ex = new ItemAccessDeniedException("Access denied");

        ResponseEntity<Map<String, String>> response = errorHandler.handleItemAccessDeniedException(ex);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    void handleMethodArgumentTypeMismatch_ShouldReturnBadRequest() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("bookingId");

        ResponseEntity<Map<String, String>> response = errorHandler.handleMethodArgumentTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
    }
}