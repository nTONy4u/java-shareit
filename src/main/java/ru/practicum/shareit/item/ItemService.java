package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemAccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public ItemService(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    public Item createItem(Item item, Long ownerId) {
        userService.getUserById(ownerId)
                .orElseThrow(() -> {
                    log.error("User not found for item creation: {}", ownerId);
                    throw new UserNotFoundException("User not found with id: " + ownerId);
                });

        item.setOwner(ownerId);
        Item createdItem = itemRepository.save(item);
        log.info("Item created with id: {}", createdItem.getId());
        return createdItem;
    }

    public Optional<Item> getItemById(Long id) {
        log.debug("Getting item by id: {}", id);
        return itemRepository.findById(id);
    }

    public Item getItemByIdOrThrow(Long id) {
        return getItemById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
    }

    public List<Item> getItemsByOwner(Long ownerId) {
        log.debug("Getting items for owner: {}", ownerId);
        return itemRepository.findByOwner(ownerId);
    }

    public Item updateItem(Long itemId, Item itemUpdates, Long ownerId) {

        Item existingItem = getItemByIdOrThrow(itemId);

        if (!existingItem.getOwner().equals(ownerId)) {
            log.error("User {} is not the owner of item {}", ownerId, itemId);
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

        Item updatedItem = itemRepository.save(existingItem);
        log.info("Item updated: {}", itemId);
        return updatedItem;
    }

    public List<Item> searchItems(String text) {
        log.debug("Searching items with text: '{}'", text);
        return itemRepository.search(text);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
        log.info("Item deleted: {}", id);
    }
}