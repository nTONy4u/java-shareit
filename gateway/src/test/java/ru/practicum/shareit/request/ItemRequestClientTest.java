package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.util.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        itemRequestClient = new ItemRequestClient("http://localhost:9090",
                new org.springframework.boot.web.client.RestTemplateBuilder());
        TestUtils.setRestTemplate(itemRequestClient, restTemplate);
    }

    @Test
    void createRequest_ShouldCallPost() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Need a drill");
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = itemRequestClient.createRequest(requestDto, 1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq(""),  // Относительный путь
                eq(org.springframework.http.HttpMethod.POST),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void getUserRequests_ShouldCallGet() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = itemRequestClient.getUserRequests(1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq(""),  // Относительный путь
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void getAllRequests_ShouldCallGetWithParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class), anyMap()
        );

        ResponseEntity<Object> response = itemRequestClient.getAllRequests(1L, 0, 10);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("/all?from={from}&size={size}"),  // Относительный путь с параметрами
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(java.util.Map.of("from", 0, "size", 10))
        );
    }

    @Test
    void getRequestById_ShouldCallGet() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = itemRequestClient.getRequestById(1L, 1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("/1"),  // Относительный путь
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(Object.class)
        );
    }
}