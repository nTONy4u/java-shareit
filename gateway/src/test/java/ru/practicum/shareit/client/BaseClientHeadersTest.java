package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaseClientHeadersTest {

    @Mock
    private RestTemplate restTemplate;

    private BaseClient baseClient;

    @BeforeEach
    void setUp() {
        baseClient = new BaseClient(restTemplate);
    }

    @Test
    void whenGetWithUserId_IncludesUserIdHeader() {
        doReturn(ResponseEntity.ok().build()).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        baseClient.get("/test", 1L);

        ArgumentCaptor<HttpEntity<?>> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq("/test"),
                any(),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertEquals("1", capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void whenPostWithUserId_IncludesUserIdHeader() {
        UserDto userDto = new UserDto(null, "Test", "test@email.com");
        doReturn(ResponseEntity.ok().build()).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        baseClient.post("/test", 1L, userDto);

        ArgumentCaptor<HttpEntity<?>> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq("/test"),
                any(),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertEquals("1", capturedEntity.getHeaders().getFirst("X-Sharer-User-Id"));
    }

    @Test
    void whenHeadersCreated_ContainsCorrectContentType() {
        doReturn(ResponseEntity.ok().build()).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        baseClient.get("/test", 1L);

        ArgumentCaptor<HttpEntity<?>> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
                eq("/test"),
                any(),
                httpEntityCaptor.capture(),
                eq(Object.class)
        );

        HttpEntity<?> capturedEntity = httpEntityCaptor.getValue();
        assertNotNull(capturedEntity.getHeaders().getContentType());
        assertEquals("application/json", capturedEntity.getHeaders().getContentType().toString());
    }
}