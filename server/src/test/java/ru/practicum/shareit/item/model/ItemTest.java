package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void item_EqualsAndHashCode_ShouldWorkCorrectly() {
        User owner1 = new User(1L, "Owner1", "owner1@example.com");
        User owner2 = new User(2L, "Owner2", "owner2@example.com");
        ItemRequest request1 = new ItemRequest(1L, "Desc1", owner1, null, null);
        ItemRequest request2 = new ItemRequest(2L, "Desc2", owner2, null, null);

        Item item1 = new Item(1L, "Item1", "Desc1", true, owner1, request1);
        Item item2 = new Item(1L, "Item1", "Desc1", true, owner1, request1);
        Item item3 = new Item(2L, "Item2", "Desc2", false, owner2, request2);

        // Проверка equals
        assertEquals(item1, item2);
        assertNotEquals(item1, item3);
        assertNotEquals(item1, null);
        assertNotEquals(item1, new Object());

        // Проверка hashCode
        assertEquals(item1.hashCode(), item2.hashCode());
        assertNotEquals(item1.hashCode(), item3.hashCode());

        // Проверка toString
        assertNotNull(item1.toString());
        assertTrue(item1.toString().contains("Item"));
    }

    @Test
    void item_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new Item());
    }

    @Test
    void item_SettersAndGetters_ShouldWork() {
        Item item = new Item();
        User owner = new User(1L, "Owner", "owner@example.com");
        ItemRequest request = new ItemRequest(1L, "Request", owner, null, null);

        item.setId(1L);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);

        assertEquals(1L, item.getId());
        assertEquals("Item", item.getName());
        assertEquals("Description", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(owner, item.getOwner());
        assertEquals(request, item.getRequest());
    }
}