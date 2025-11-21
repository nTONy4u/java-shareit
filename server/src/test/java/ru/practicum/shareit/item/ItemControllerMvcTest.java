package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerMvcTest {

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
    void createItem_ShouldReturnCreatedItem() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Drill", "Powerful drill", true, null, null, null, null);
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        ItemDto responseDto = new ItemDto(1L, "Drill", "Powerful drill", true, null, null, null, null);

        when(userService.getUserById(anyLong())).thenReturn(owner);
        when(itemMapper.toItem(any(ItemDto.class), any(User.class))).thenReturn(item);
        when(itemService.createItem(any(Item.class), anyLong())).thenReturn(item);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void getItem_ShouldReturnItem() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        ItemDto responseDto = new ItemDto(1L, "Drill", "Powerful drill", true, null, List.of(), null, null);

        when(itemService.getItemById(anyLong())).thenReturn(item);
        when(commentService.getCommentsByItemId(anyLong())).thenReturn(List.of());
        when(itemMapper.toItemDto(any(Item.class), any(), any())).thenReturn(responseDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void getItemsByOwner_ShouldReturnItems() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);
        ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, null, null, null, null);

        when(itemService.getItemsByOwner(anyLong())).thenReturn(List.of(item));
        when(itemBookingInfoService.getLastBookingForItem(anyLong())).thenReturn(null);
        when(itemBookingInfoService.getNextBookingForItem(anyLong())).thenReturn(null);
        when(itemMapper.toItemDto(any(Item.class), any(), any())).thenReturn(itemDto);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Item"));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Updated Drill", "Updated description", false, null, null, null, null);
        User owner = new User(1L, "Owner", "owner@example.com");
        Item updatedItem = new Item(1L, "Updated Drill", "Updated description", false, owner, null);
        ItemDto responseDto = new ItemDto(1L, "Updated Drill", "Updated description", false, null, null, null, null);

        when(userService.getUserById(anyLong())).thenReturn(owner);
        when(itemMapper.toItem(any(ItemDto.class), any(User.class))).thenReturn(updatedItem);
        when(itemService.updateItem(anyLong(), any(Item.class), anyLong())).thenReturn(updatedItem);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Drill"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void searchItems_ShouldReturnMatchingItems() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Drill", "Powerful electric drill", true, owner, null);
        ItemDto responseDto = new ItemDto(1L, "Drill", "Powerful electric drill", true, null, null, null, null);

        when(itemService.searchItems(anyString())).thenReturn(List.of(item));
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(responseDto);

        mockMvc.perform(get("/items/search")
                        .param("text", "drill")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void addComment_ShouldReturnCreatedComment() throws Exception {
        CommentCreateDto commentDto = new CommentCreateDto("Great item!");
        User author = new User(1L, "Author", "author@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, author, null);
        Comment comment = new Comment(1L, "Great item!", item, author, LocalDateTime.now());

        when(commentService.createComment(any(CommentCreateDto.class), anyLong(), anyLong()))
                .thenReturn(comment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());
    }
}