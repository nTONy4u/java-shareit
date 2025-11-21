package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long ownerId);

    Item getItemById(Long id);

    List<Item> getItemsByOwner(Long ownerId);

    Item updateItem(Long itemId, Item itemUpdates, Long ownerId);

    List<Item> searchItems(String text);

    void deleteItem(Long id);

    List<Item> getItemsByRequestId(Long requestId);
}