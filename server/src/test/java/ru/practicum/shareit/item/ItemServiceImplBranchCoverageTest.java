package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ItemAccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplBranchCoverageTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void updateItem_AllFieldsNull_ShouldReturnOriginalItem() {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item existingItem = new Item(1L, "Original", "Original Desc", true, owner, null);
        Item updates = new Item(); // Все поля null

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(existingItem);

        Item result = itemService.updateItem(1L, updates, 1L);

        assertEquals("Original", result.getName());
        assertEquals("Original Desc", result.getDescription());
        assertTrue(result.getAvailable());
    }

    @Test
    void updateItem_OnlyNameProvided_ShouldUpdateOnlyName() {
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
    void updateItem_OnlyDescriptionProvided_ShouldUpdateOnlyDescription() {
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
    void updateItem_OnlyAvailableProvided_ShouldUpdateOnlyAvailable() {
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
    void updateItem_NotOwner_ShouldThrowException() {
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
    void getItemById_NotFound_ShouldThrowException() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(999L));
    }

    @Test
    void searchItems_NullText_ShouldReturnEmptyList() {
        List<Item> result = itemService.searchItems(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_EmptyText_ShouldReturnEmptyList() {
        List<Item> result = itemService.searchItems("");
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_BlankText_ShouldReturnEmptyList() {
        List<Item> result = itemService.searchItems("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItems_ValidText_ShouldReturnResults() {
        when(itemRepository.searchAvailableItems("drill")).thenReturn(List.of(new Item()));

        List<Item> result = itemService.searchItems("drill");

        assertFalse(result.isEmpty());
    }

    @Test
    void deleteItem_ShouldCallRepository() {
        itemService.deleteItem(1L);
        verify(itemRepository).deleteById(1L);
    }
}