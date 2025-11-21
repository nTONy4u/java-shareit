package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ItemAccessDeniedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplFullCoverageTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void searchItems_WithNullText_ShouldReturnEmptyList() {
        List<Item> result = itemService.searchItems(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_WithEmptyText_ShouldReturnEmptyList() {
        List<Item> result = itemService.searchItems("");
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_WithBlankText_ShouldReturnEmptyList() {
        List<Item> result = itemService.searchItems("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void getItemsByRequestId_ShouldReturnItems() {
        Item item = new Item(1L, "Item", "Description", true, new User(), null);
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of(item));

        List<Item> result = itemService.getItemsByRequestId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getItemsByRequestId_NoItems_ShouldReturnEmptyList() {
        when(itemRepository.findByRequestId(1L)).thenReturn(List.of());

        List<Item> result = itemService.getItemsByRequestId(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateItem_WithOnlyNameUpdate_ShouldUpdateOnlyName() {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item existingItem = new Item(1L, "Original", "Original Desc", true, owner, null);
        Item updates = new Item();
        updates.setName("Updated Name");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        Item result = itemService.updateItem(1L, updates, 1L);

        assertEquals("Updated Name", result.getName());
        assertEquals("Original Desc", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void updateItem_WithOnlyDescriptionUpdate_ShouldUpdateOnlyDescription() {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item existingItem = new Item(1L, "Original", "Original Desc", true, owner, null);
        Item updates = new Item();
        updates.setDescription("Updated Description");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        Item result = itemService.updateItem(1L, updates, 1L);

        assertEquals("Original", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void updateItem_WithOnlyAvailableUpdate_ShouldUpdateOnlyAvailable() {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item existingItem = new Item(1L, "Original", "Original Desc", true, owner, null);
        Item updates = new Item();
        updates.setAvailable(false);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        Item result = itemService.updateItem(1L, updates, 1L);

        assertEquals("Original", result.getName());
        assertEquals("Original Desc", result.getDescription());
        assertFalse(result.getAvailable());
    }

    @Test
    void updateItem_NonOwnerTriesToUpdate_ShouldThrowException() {
        User owner = new User(1L, "Owner", "owner@example.com");
        User otherUser = new User(2L, "Other", "other@example.com");
        Item existingItem = new Item(1L, "Item", "Description", true, owner, null);
        Item updates = new Item();
        updates.setName("Hacked");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        assertThrows(ItemAccessDeniedException.class,
                () -> itemService.updateItem(1L, updates, 2L));
    }

    @Test
    void searchItems_WithSpecialCharacters_ShouldHandleCorrectly() {
        String searchText = "test-item@special.com";
        when(itemRepository.searchAvailableItems(searchText)).thenReturn(List.of());

        List<Item> result = itemService.searchItems(searchText);

        assertTrue(result.isEmpty());
        verify(itemRepository).searchAvailableItems(searchText);
    }
}