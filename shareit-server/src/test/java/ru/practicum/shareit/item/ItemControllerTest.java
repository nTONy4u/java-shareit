package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemMapper itemMapper;

    @MockBean
    private ItemBookingInfoService itemBookingInfoService;

    @Test
    void createItem_ValidItem_ShouldReturnCreatedItem() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Drill", "Powerful drill", true, null, null, null, null);
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        ItemDto responseDto = new ItemDto(1L, "Drill", "Powerful drill", true, null, null, null, null);

        when(userService.getUserById(ArgumentMatchers.eq(1L))).thenReturn(owner);
        when(itemMapper.toItem(
                ArgumentMatchers.any(ItemDto.class),
                ArgumentMatchers.any(User.class))
        ).thenReturn(item);
        when(itemService.createItem(
                ArgumentMatchers.any(Item.class),
                ArgumentMatchers.eq(1L))
        ).thenReturn(item);
        when(itemMapper.toItemDto(ArgumentMatchers.any(Item.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Drill")));
    }

    @Test
    void getItem_ExistingItem_ShouldReturnItem() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        ItemDto responseDto = new ItemDto(1L, "Drill", "Powerful drill", true, null, List.of(), null, null);

        when(itemService.getItemById(ArgumentMatchers.eq(1L))).thenReturn(item);
        when(commentService.getCommentsByItemId(ArgumentMatchers.eq(1L))).thenReturn(List.of());
        when(itemMapper.toItemDto(
                ArgumentMatchers.any(Item.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.any())
        ).thenReturn(responseDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Drill")));
    }

    @Test
    void getItem_NonExistingItem_ShouldReturnNotFound() throws Exception {
        when(itemService.getItemById(ArgumentMatchers.eq(999L)))
                .thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(get("/items/999")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemsByOwner_UserWithItems_ShouldReturnItemList() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item1 = new Item(1L, "Item 1", "Description 1", true, owner, null);
        Item item2 = new Item(2L, "Item 2", "Description 2", true, owner, null);
        List<Item> items = Arrays.asList(item1, item2);
        ItemDto dto1 = new ItemDto(1L, "Item 1", "Description 1", true, null, null, null, null);
        ItemDto dto2 = new ItemDto(2L, "Item 2", "Description 2", true, null, null, null, null);

        when(itemService.getItemsByOwner(ArgumentMatchers.eq(1L))).thenReturn(items);
        when(itemBookingInfoService.getLastBookingForItem(ArgumentMatchers.anyLong())).thenReturn(null);
        when(itemBookingInfoService.getNextBookingForItem(ArgumentMatchers.anyLong())).thenReturn(null);
        when(itemMapper.toItemDto(
                ArgumentMatchers.any(Item.class),
                ArgumentMatchers.any(),
                ArgumentMatchers.any())
        )
                .thenReturn(dto1)
                .thenReturn(dto2);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updateItem_ValidUpdate_ShouldReturnUpdatedItem() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Updated Drill", "Updated description", false, null, null, null, null);
        User owner = new User(1L, "Owner", "owner@example.com");
        Item updatedItem = new Item(1L, "Updated Drill", "Updated description", false, owner, null);
        ItemDto responseDto = new ItemDto(1L, "Updated Drill", "Updated description", false, null, null, null, null);

        when(userService.getUserById(ArgumentMatchers.eq(1L))).thenReturn(owner);
        when(itemMapper.toItem(
                ArgumentMatchers.any(ItemDto.class),
                ArgumentMatchers.any(User.class))
        ).thenReturn(updatedItem);
        when(itemService.updateItem(
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.any(Item.class),
                ArgumentMatchers.eq(1L))
        ).thenReturn(updatedItem);
        when(itemMapper.toItemDto(ArgumentMatchers.any(Item.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Drill")))
                .andExpect(jsonPath("$.available", is(false)));
    }

    @Test
    void searchItems_WithText_ShouldReturnMatchingItems() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Drill", "Powerful electric drill", true, owner, null);
        ItemDto responseDto = new ItemDto(1L, "Drill", "Powerful electric drill", true, null, null, null, null);

        when(itemService.searchItems(ArgumentMatchers.eq("drill"))).thenReturn(List.of(item));
        when(itemMapper.toItemDto(ArgumentMatchers.any(Item.class))).thenReturn(responseDto);

        mockMvc.perform(get("/items/search")
                        .param("text", "drill")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Drill")));
    }

    @Test
    void addComment_ValidComment_ShouldReturnCreatedComment() throws Exception {
        CommentCreateDto commentDto = new CommentCreateDto("Great item!");
        User author = new User(1L, "Author", "author@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, author, null);
        Comment comment = new Comment(1L, "Great item!", item, author, LocalDateTime.now());

        when(commentService.createComment(
                ArgumentMatchers.any(CommentCreateDto.class),
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.eq(1L))
        ).thenReturn(comment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());
    }
}