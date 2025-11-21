package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseClientTest {

    @Mock
    private RestTemplate restTemplate;

    private BaseClient baseClient;

    @BeforeEach
    void setUp() {
        baseClient = new BaseClient(restTemplate);
    }

    @Test
    void get_WithoutParameters_ReturnsResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = baseClient.get("/test");

        assertEquals(expectedResponse, response);
    }

    @Test
    void get_WithUserId_ReturnsResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = baseClient.get("/test", 1L);

        assertEquals(expectedResponse, response);
    }

    @Test
    void post_WithBody_ReturnsResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = baseClient.post("/test", "test body");

        assertEquals(expectedResponse, response);
    }

    @Test
    void patch_WithUserIdAndBody_ReturnsResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = baseClient.patch("/test", 1L, "test body");

        assertEquals(expectedResponse, response);
    }

    @Test
    void put_WithUserIdAndBody_ReturnsResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = baseClient.put("/test", 1L, "test body");

        assertEquals(expectedResponse, response);
    }

    @Test
    void delete_WithUserId_ReturnsResponse() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = baseClient.delete("/test", 1L);

        assertEquals(expectedResponse, response);
    }

    @Test
    void whenHttpClientErrorException_ReturnsErrorResponse() {
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");

        doThrow(exception).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = baseClient.get("/test", 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}