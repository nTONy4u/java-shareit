package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {

    @Test
    void toDto_ShouldMapCorrectly() {
        User requestor = new User(1L, "Requestor", "requestor@example.com");
        User owner = new User(2L, "Owner", "owner@example.com");

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, request);
        request.setItems(List.of(item));

        ItemRequestDto dto = ItemRequestMapper.toDto(request);

        assertNotNull(dto);
        assertEquals(request.getId(), dto.getId());
        assertEquals(request.getDescription(), dto.getDescription());
        assertEquals(request.getCreated(), dto.getCreated());
        assertFalse(dto.getItems().isEmpty());
        assertEquals(item.getId(), dto.getItems().get(0).getId());
        assertEquals(item.getName(), dto.getItems().get(0).getName());
    }

    @Test
    void toDto_WithNullItems_ShouldHandleCorrectly() {
        User requestor = new User(1L, "Requestor", "requestor@example.com");

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        request.setItems(null);

        ItemRequestDto dto = ItemRequestMapper.toDto(request);

        assertNotNull(dto);
        assertNull(dto.getItems());
    }

    @Test
    void toDto_WithEmptyItems_ShouldHandleCorrectly() {
        User requestor = new User(1L, "Requestor", "requestor@example.com");

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need a drill");
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        request.setItems(List.of());

        ItemRequestDto dto = ItemRequestMapper.toDto(request);

        assertNotNull(dto);
        assertNotNull(dto.getItems());
        assertTrue(dto.getItems().isEmpty());
    }
}