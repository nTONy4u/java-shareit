package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.util.TestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    @Mock
    private RestTemplate restTemplate;

    private BookingClient bookingClient;

    @BeforeEach
    void setUp() {
        bookingClient = new BookingClient("http://localhost:9090",
                new org.springframework.boot.web.client.RestTemplateBuilder());
        TestUtils.setRestTemplate(bookingClient, restTemplate);
    }

    @Test
    void createBooking_ShouldCallPost() {
        BookingCreateDto bookingDto = new BookingCreateDto(1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2));
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = bookingClient.createBooking(bookingDto, 1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void approveBooking_ShouldCallPatchWithParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class), anyMap()
        );

        ResponseEntity<Object> response = bookingClient.approveBooking(1L, true, 1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("/1?approved={approved}"),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(),
                eq(Object.class),
                eq(java.util.Map.of("approved", true))
        );
    }

    @Test
    void cancelBooking_ShouldCallPatch() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = bookingClient.cancelBooking(1L, 1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("/1/cancel"),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void getBooking_ShouldCallGet() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = bookingClient.getBooking(1L, 1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void getUserBookings_ShouldCallGetWithParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class), anyMap()
        );

        ResponseEntity<Object> response = bookingClient.getUserBookings(1L, "ALL", 0, 10);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("?state={state}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(java.util.Map.of("state", "ALL", "from", 0, "size", 10))
        );
    }

    @Test
    void getOwnerBookings_ShouldCallGetWithParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class), anyMap()
        );

        ResponseEntity<Object> response = bookingClient.getOwnerBookings(1L, "ALL", 0, 10);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("/owner?state={state}&from={from}&size={size}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(java.util.Map.of("state", "ALL", "from", 0, "size", 10))
        );
    }
}