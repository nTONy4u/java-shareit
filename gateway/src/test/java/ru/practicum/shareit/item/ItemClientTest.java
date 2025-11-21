package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        itemClient = new ItemClient("http://localhost:9090",
                new org.springframework.boot.web.client.RestTemplateBuilder());
        TestUtils.setRestTemplate(itemClient, restTemplate);
    }

    @Test
    void createItem_ShouldCallPost() {
        ItemDto itemDto = new ItemDto(null, "Test Item", "Test Description", true, null, null, null, null);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = itemClient.createItem(itemDto, 1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.POST),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void updateItem_ShouldCallPatch() {
        ItemDto itemDto = new ItemDto(null, "Updated Item", "Updated Description", true, null, null, null, null);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = itemClient.updateItem(1L, itemDto, 1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(org.springframework.http.HttpMethod.PATCH),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void getItem_ShouldCallGet() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = itemClient.getItem(1L, 1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("/1"),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void getItemsByOwner_ShouldCallGet() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = itemClient.getItemsByOwner(1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq(""),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(Object.class)
        );
    }

    @Test
    void searchItems_ShouldCallGetWithParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class), anyMap()
        );

        ResponseEntity<Object> response = itemClient.searchItems("test", 1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("/search?text={text}"),
                eq(org.springframework.http.HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(java.util.Map.of("text", "test"))
        );
    }

    @Test
    void addComment_ShouldCallPost() {
        CommentCreateDto commentDto = new CommentCreateDto("Great item!");
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        doReturn(expectedResponse).when(restTemplate).exchange(
                anyString(), any(), any(), eq(Object.class)
        );

        ResponseEntity<Object> response = itemClient.addComment(1L, commentDto, 1L);

        assertEquals(expectedResponse, response);
        verify(restTemplate).exchange(
                eq("/1/comment"),
                eq(org.springframework.http.HttpMethod.POST),
                any(),
                eq(Object.class)
        );
    }
}