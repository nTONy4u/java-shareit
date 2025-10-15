package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating item for user {}: {}", userId, itemDto);
        Item item = ItemMapper.toItem(itemDto, userId);
        Item createdItem = itemService.createItem(item, userId);
        log.info("Item created with id: {}", createdItem.getId());
        return ResponseEntity.ok(ItemMapper.toItemDto(createdItem));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId,
                                              @Valid @RequestBody ItemUpdateDto itemUpdateDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Updating item {} for user {}: {}", itemId, userId, itemUpdateDto);

        Item itemUpdates = ItemMapper.toItem(itemUpdateDto, userId);
        Item updatedItem = itemService.updateItem(itemId, itemUpdates, userId);
        log.info("Item updated: {}", itemId);
        return ResponseEntity.ok(ItemMapper.toItemDto(updatedItem));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId) {
        log.info("Getting item by id: {}", itemId);
        Item item = itemService.getItemById(itemId)
                .orElseThrow(() -> {
                    log.warn("Item not found: {}", itemId);
                    return new ItemNotFoundException("Item not found with id: " + itemId);
                });
        return ResponseEntity.ok(ItemMapper.toItemDto(item));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting items for owner: {}", userId);
        List<ItemDto> items = itemService.getItemsByOwner(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Found {} items for owner {}", items.size(), userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        log.info("Searching items with text: '{}'", text);
        List<ItemDto> items = itemService.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Found {} items for search '{}'", items.size(), text);
        return ResponseEntity.ok(items);
    }
}
