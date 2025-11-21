package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplEdgeCasesTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void updateItem_WithNullUpdates_ShouldThrowException() {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item existingItem = new Item(1L, "Item", "Description", true, owner, null);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        assertThrows(NullPointerException.class,
                () -> itemService.updateItem(1L, null, 1L));
    }

    @Test
    void searchItems_WithSpecialCharacters_ShouldHandleCorrectly() {
        String searchText = "test-item@special.com";
        when(itemRepository.searchAvailableItems(searchText)).thenReturn(List.of());

        List<Item> result = itemService.searchItems(searchText);

        assertTrue(result.isEmpty());
        verify(itemRepository).searchAvailableItems(searchText);
    }

    @Test
    void getItemsByRequestId_WithInvalidId_ShouldReturnEmptyList() {
        when(itemRepository.findByRequestId(-1L)).thenReturn(List.of());

        List<Item> result = itemService.getItemsByRequestId(-1L);

        assertTrue(result.isEmpty());
    }
}