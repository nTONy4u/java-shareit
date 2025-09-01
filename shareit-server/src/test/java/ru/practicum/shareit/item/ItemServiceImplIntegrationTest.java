package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemAccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private User owner;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userService.createUser(owner);

        anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");
        anotherUser = userService.createUser(anotherUser);
    }

    @Test
    void createItem_ValidItem_ShouldCreateItem() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);

        Item createdItem = itemService.createItem(item, owner.getId());

        assertNotNull(createdItem.getId());
        assertEquals("Test Item", createdItem.getName());
        assertEquals("Test Description", createdItem.getDescription());
        assertTrue(createdItem.getAvailable());
        assertEquals(owner.getId(), createdItem.getOwner().getId());
    }

    @Test
    void getItemById_ExistingItem_ShouldReturnItem() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        Item createdItem = itemService.createItem(item, owner.getId());

        Item foundItem = itemService.getItemById(createdItem.getId());

        assertEquals(createdItem.getId(), foundItem.getId());
        assertEquals("Test Item", foundItem.getName());
    }

    @Test
    void getItemById_NonExistingItem_ShouldThrowException() {
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(999L));
    }

    @Test
    void getItemsByOwner_UserWithItems_ShouldReturnItems() {
        Item item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(owner);
        itemService.createItem(item1, owner.getId());

        Item item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(true);
        item2.setOwner(owner);
        itemService.createItem(item2, owner.getId());

        List<Item> items = itemService.getItemsByOwner(owner.getId());

        assertEquals(2, items.size());
    }

    @Test
    void updateItem_OwnerUpdatesItem_ShouldUpdateItem() {
        Item item = new Item();
        item.setName("Original Name");
        item.setDescription("Original Description");
        item.setAvailable(true);
        item.setOwner(owner);
        Item createdItem = itemService.createItem(item, owner.getId());

        Item updates = new Item();
        updates.setName("Updated Name");
        updates.setDescription("Updated Description");
        updates.setAvailable(false);

        Item updatedItem = itemService.updateItem(createdItem.getId(), updates, owner.getId());

        assertEquals("Updated Name", updatedItem.getName());
        assertEquals("Updated Description", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void updateItem_NonOwnerUpdatesItem_ShouldThrowException() {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        Item createdItem = itemService.createItem(item, owner.getId());

        Item updates = new Item();
        updates.setName("Updated Name");

        assertThrows(ItemAccessDeniedException.class,
                () -> itemService.updateItem(createdItem.getId(), updates, anotherUser.getId()));
    }

    @Test
    void searchItems_AvailableItems_ShouldReturnMatchingItems() {
        Item item1 = new Item();
        item1.setName("Drill Machine");
        item1.setDescription("Powerful electric drill");
        item1.setAvailable(true);
        item1.setOwner(owner);
        itemService.createItem(item1, owner.getId());

        Item item2 = new Item();
        item2.setName("Hammer");
        item2.setDescription("Heavy duty hammer");
        item2.setAvailable(false);
        item2.setOwner(owner);
        itemService.createItem(item2, owner.getId());

        List<Item> foundItems = itemService.searchItems("drill");

        assertEquals(1, foundItems.size());
        assertEquals("Drill Machine", foundItems.get(0).getName());
    }

    @Test
    void searchItems_EmptyText_ShouldReturnEmptyList() {
        List<Item> foundItems = itemService.searchItems("");

        assertTrue(foundItems.isEmpty());
    }
}