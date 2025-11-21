package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User requestor;

    @BeforeEach
    void setUp() {
        owner = createUser("Owner", "owner@example.com");
        requestor = createUser("Requestor", "requestor@example.com");
    }

    @Test
    void findByOwnerIdOrderById_ShouldReturnOwnerItems() {
        Item item1 = createItem("Item 1", "Description 1", true, owner);
        Item item2 = createItem("Item 2", "Description 2", true, owner);

        List<Item> result = itemRepository.findByOwnerIdOrderById(owner.getId());

        assertEquals(2, result.size());
        assertEquals("Item 1", result.get(0).getName());
        assertEquals("Item 2", result.get(1).getName());
    }

    @Test
    void searchAvailableItems_ShouldReturnMatchingAvailableItems() {
        createItem("Drill Machine", "Powerful electric drill", true, owner);
        createItem("Hammer", "Heavy hammer", true, owner);
        createItem("Broken Drill", "Not working", false, owner);

        List<Item> result = itemRepository.searchAvailableItems("drill");

        assertEquals(1, result.size());
        assertEquals("Drill Machine", result.get(0).getName());
    }

    @Test
    void searchAvailableItems_CaseInsensitive_ShouldReturnItems() {
        createItem("DRILL", "Powerful tool", true, owner);

        List<Item> result = itemRepository.searchAvailableItems("drill");

        assertEquals(1, result.size());
    }

    @Test
    void searchAvailableItems_PartialMatch_ShouldReturnItems() {
        createItem("Electric Drill", "Powerful tool", true, owner);

        List<Item> result = itemRepository.searchAvailableItems("elec");

        assertEquals(1, result.size());
    }

    @Test
    void findByRequestId_ShouldReturnItemsForRequest() {
        ItemRequest request = createRequest("Need tools", requestor);
        Item item = createItemWithRequest("Provided Drill", "Good drill", true, owner, request);

        List<Item> result = itemRepository.findByRequestId(request.getId());

        assertEquals(1, result.size());
        assertEquals("Provided Drill", result.get(0).getName());
    }

    @Test
    void findByRequestId_NoItems_ShouldReturnEmptyList() {
        List<Item> result = itemRepository.findByRequestId(999L);
        assertTrue(result.isEmpty());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return entityManager.persistAndFlush(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return entityManager.persistAndFlush(item);
    }

    private Item createItemWithRequest(String name, String description, Boolean available, User owner, ItemRequest request) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(request);
        return entityManager.persistAndFlush(item);
    }

    private ItemRequest createRequest(String description, User requestor) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return entityManager.persistAndFlush(request);
    }
}