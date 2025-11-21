package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemAccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Transactional
    public Item createItem(Item item, Long ownerId) {
        userService.getUserById(ownerId);
        Item createdItem = itemRepository.save(item);
        log.info("Item created with id: {}", createdItem.getId());
        return createdItem;
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
    }

    public List<Item> getItemsByOwner(Long ownerId) {
        log.debug("Getting items for owner: {}", ownerId);
        userService.getUserById(ownerId);
        return itemRepository.findByOwnerIdOrderById(ownerId);
    }

    @Transactional
    public Item updateItem(Long itemId, Item itemUpdates, Long ownerId) {
        Item existingItem = getItemById(itemId);

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new ItemAccessDeniedException("User is not the owner of this item");
        }

        if (itemUpdates.getName() != null) {
            log.debug("Updating name for item {}: {}", itemId, itemUpdates.getName());
            existingItem.setName(itemUpdates.getName());
        }
        if (itemUpdates.getDescription() != null) {
            log.debug("Updating description for item {}: {}", itemId, itemUpdates.getDescription());
            existingItem.setDescription(itemUpdates.getDescription());
        }
        if (itemUpdates.getAvailable() != null) {
            log.debug("Updating available status for item {}: {}", itemId, itemUpdates.getAvailable());
            existingItem.setAvailable(itemUpdates.getAvailable());
        }

        return itemRepository.save(existingItem);
    }

    public List<Item> searchItems(String text) {
        log.debug("Searching items with text: '{}'", text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchAvailableItems(text);
    }

    @Transactional
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
        log.info("Item deleted: {}", id);
    }

    public List<Item> getItemsByRequestId(Long requestId) {
        return itemRepository.findByRequestId(requestId);
    }
}