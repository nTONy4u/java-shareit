package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    Item createItem(Item item, Long ownerId);

    Item getItemById(Long id);

    List<Item> getItemsByOwner(Long ownerId);

    Item updateItem(Long itemId, Item itemUpdates, Long ownerId);

    List<Item> searchItems(String text);

    void deleteItem(Long id);

    List<Item> getItemsByRequestId(Long requestId);

    Map<Long, List<Item>> getItemsByRequestIds(List<Long> requestIds);
}