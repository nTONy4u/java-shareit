package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AllExceptionsTest {

    @Test
    void userNotFoundException_ShouldCreateWithMessage() {
        UserNotFoundException exception = new UserNotFoundException("User not found");
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void itemNotFoundException_ShouldCreateWithMessage() {
        ItemNotFoundException exception = new ItemNotFoundException("Item not found");
        assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void bookingNotFoundException_ShouldCreateWithMessage() {
        BookingNotFoundException exception = new BookingNotFoundException("Booking not found");
        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void itemRequestNotFoundException_ShouldCreateWithMessage() {
        ItemRequestNotFoundException exception = new ItemRequestNotFoundException("Request not found");
        assertEquals("Request not found", exception.getMessage());
    }

    @Test
    void emailAlreadyExistsException_ShouldCreateWithMessage() {
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException("Email exists");
        assertEquals("Email exists", exception.getMessage());
    }

    @Test
    void validationException_ShouldCreateWithMessage() {
        ValidationException exception = new ValidationException("Validation error");
        assertEquals("Validation error", exception.getMessage());
    }

    @Test
    void itemAccessDeniedException_ShouldCreateWithMessage() {
        ItemAccessDeniedException exception = new ItemAccessDeniedException("Access denied");
        assertEquals("Access denied", exception.getMessage());
    }

    @Test
    void allExceptions_ShouldBeRuntimeExceptions() {
        assertTrue(new UserNotFoundException("") instanceof RuntimeException);
        assertTrue(new ItemNotFoundException("") instanceof RuntimeException);
        assertTrue(new BookingNotFoundException("") instanceof RuntimeException);
        assertTrue(new ItemRequestNotFoundException("") instanceof RuntimeException);
        assertTrue(new EmailAlreadyExistsException("") instanceof RuntimeException);
        assertTrue(new ValidationException("") instanceof RuntimeException);
        assertTrue(new ItemAccessDeniedException("") instanceof RuntimeException);
    }
}