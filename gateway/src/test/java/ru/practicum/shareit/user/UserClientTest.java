package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    private UserClient userClient;

    @BeforeEach
    void setUp() {
        userClient = new UserClient("http://localhost:9090",
                new org.springframework.boot.web.client.RestTemplateBuilder());
        TestUtils.setRestTemplate(userClient, restTemplate);
    }

    @Test
    void createUser_ShouldCallPost() {
        UserDto userDto = new UserDto(null, "Test User", "test@email.com");
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = userClient.createUser(userDto);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void updateUser_ShouldCallPatch() {
        UserDto userDto = new UserDto(null, "Updated User", "updated@email.com");
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = userClient.updateUser(1L, userDto);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void getUser_ShouldCallGet() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = userClient.getUser(1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void getAllUsers_ShouldCallGet() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = userClient.getAllUsers();

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void deleteUser_ShouldCallDelete() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = userClient.deleteUser(1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(org.springframework.http.HttpMethod.DELETE),
                any(),
                eq(Object.class)
        );
    }
}