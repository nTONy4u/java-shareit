package ru.practicum.shareit.exception;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ErrorHandlerTest {

    @Autowired
    private ErrorHandler errorHandler;

    @Test
    void handleMissingRequestHeaderException() {
        MissingRequestHeaderException exception = new MissingRequestHeaderException("X-Sharer-User-Id", null);

        ResponseEntity<Map<String, String>> response = errorHandler.handleMissingRequestHeaderException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Required header 'X-Sharer-User-Id' is missing", response.getBody().get("error"));
    }

    @Test
    void handleHttpClientErrorException() {
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found");

        ResponseEntity<Map<String, String>> response = errorHandler.handleHttpClientErrorException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not Found", response.getBody().get("error"));
    }

    @Test
    void handleValidationException() {
        ValidationException exception = new ValidationException("Validation failed");

        ResponseEntity<Map<String, String>> response = errorHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().get("error"));
    }

    @Test
    void handleException() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<Map<String, String>> response = errorHandler.handleException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal server error", response.getBody().get("error"));
    }
}