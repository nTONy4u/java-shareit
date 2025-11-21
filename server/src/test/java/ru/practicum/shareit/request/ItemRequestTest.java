package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    @Test
    void itemRequest_EqualsAndHashCode_ShouldWorkCorrectly() {
        User requestor1 = new User(1L, "Requestor1", "requestor1@example.com");
        User requestor2 = new User(2L, "Requestor2", "requestor2@example.com");
        LocalDateTime created = LocalDateTime.now();

        ItemRequest request1 = new ItemRequest(1L, "Desc1", requestor1, created, null);
        ItemRequest request2 = new ItemRequest(1L, "Desc1", requestor1, created, null);
        ItemRequest request3 = new ItemRequest(2L, "Desc2", requestor2, created, null);

        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertNotEquals(request1, null);
        assertNotEquals(request1, new Object());

        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());

        assertNotNull(request1.toString());
        assertTrue(request1.toString().contains("ItemRequest"));
    }

    @Test
    void itemRequest_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new ItemRequest());
    }

    @Test
    void itemRequest_SettersAndGetters_ShouldWork() {
        ItemRequest request = new ItemRequest();
        User requestor = new User(1L, "Requestor", "requestor@example.com");
        User owner = new User(2L, "Owner", "owner@example.com");
        LocalDateTime created = LocalDateTime.now();
        Item item = new Item(1L, "Item", "Desc", true, owner, request);
        List<Item> items = List.of(item);

        request.setId(1L);
        request.setDescription("Description");
        request.setRequestor(requestor);
        request.setCreated(created);
        request.setItems(items);

        assertEquals(1L, request.getId());
        assertEquals("Description", request.getDescription());
        assertEquals(requestor, request.getRequestor());
        assertEquals(created, request.getCreated());
        assertEquals(items, request.getItems());
        assertEquals(1, request.getItems().size());
    }
}