package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemAccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Item createItem(Item item, Long ownerId) {
        userService.getUserById(ownerId);
        return itemRepository.save(item);
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item not found with id: " + id));
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        userService.getUserById(ownerId);
        return itemRepository.findByOwnerIdOrderById(ownerId);
    }

    @Override
    @Transactional
    public Item updateItem(Long itemId, Item itemUpdates, Long ownerId) {
        Item existingItem = getItemById(itemId);

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new ItemAccessDeniedException("User is not the owner of this item");
        }

        if (itemUpdates.getName() != null) {
            existingItem.setName(itemUpdates.getName());
        }
        if (itemUpdates.getDescription() != null) {
            existingItem.setDescription(itemUpdates.getDescription());
        }
        if (itemUpdates.getAvailable() != null) {
            existingItem.setAvailable(itemUpdates.getAvailable());
        }

        return itemRepository.save(existingItem);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchAvailableItems(text);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<Item> getItemsByRequestId(Long requestId) {
        return itemRepository.findByRequestId(requestId);
    }

    @Override
    public Map<Long, List<Item>> getItemsByRequestIds(List<Long> requestIds) {
        if (requestIds == null || requestIds.isEmpty()) {
            return Map.of();
        }

        List<Item> allItems = itemRepository.findByRequestIdIn(requestIds);

        return allItems.stream()
                .filter(item -> item.getRequest() != null)
                .collect(Collectors.groupingBy(
                    item -> item.getRequest().getId(),
                    Collectors.toList()
                ));
    }
}