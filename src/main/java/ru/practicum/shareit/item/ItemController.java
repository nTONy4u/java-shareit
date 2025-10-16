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
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemController(ItemService itemService, CommentService commentService,
                          UserService userService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.commentService = commentService;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating item for user {}: {}", userId, itemDto);
        User owner = userService.getUserById(userId);
        Item item = itemMapper.toItem(itemDto, owner);
        Item createdItem = itemService.createItem(item, userId);
        log.info("Item created with id: {}", createdItem.getId());
        return ResponseEntity.ok(itemMapper.toItemDto(createdItem));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long itemId,
            @Valid @RequestBody CommentCreateDto commentDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Adding comment to item {} by user {}: {}", itemId, userId, commentDto);
        Comment comment = commentService.createComment(commentDto, itemId, userId);
        return ResponseEntity.ok(CommentMapper.toDto(comment));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId,
                                              @Valid @RequestBody ItemUpdateDto itemUpdateDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Updating item {} for user {}: {}", itemId, userId, itemUpdateDto);

        User owner = userService.getUserById(userId);
        Item itemUpdates = itemMapper.toItem(itemUpdateDto, owner);
        Item updatedItem = itemService.updateItem(itemId, itemUpdates, userId);
        log.info("Item updated: {}", itemId);
        return ResponseEntity.ok(itemMapper.toItemDto(updatedItem));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting item by id: {} for user {}", itemId, userId);
        Item item = itemService.getItemById(itemId);
        List<Comment> comments = commentService.getCommentsByItemId(itemId);

        ItemDto itemDto = itemMapper.toItemDto(item, userId);
        itemDto.setComments(comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(itemDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting items for owner: {}", userId);
        List<ItemDto> items = itemService.getItemsByOwner(userId).stream()
                .map(item -> itemMapper.toItemDto(item, userId))
                .collect(Collectors.toList());
        log.info("Found {} items for owner {}", items.size(), userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        log.info("Searching items with text: '{}'", text);
        List<ItemDto> items = itemService.searchItems(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Found {} items for search '{}'", items.size(), text);
        return ResponseEntity.ok(items);
    }
}
