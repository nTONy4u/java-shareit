package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerEdgeCasesTest {

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
    void getItem_NotFound_ShouldReturnNotFound() throws Exception {
        when(itemService.getItemById(anyLong()))
                .thenThrow(new ItemNotFoundException("Item not found"));

        mockMvc.perform(get("/items/999")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void addComment_UserHasNotBooked_ShouldReturnBadRequest() throws Exception {
        CommentCreateDto commentDto = new CommentCreateDto("Great item!");

        when(commentService.createComment(any(), anyLong(), anyLong()))
                .thenThrow(new ValidationException("User can only comment on items they have booked"));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_NotOwner_ShouldReturnForbidden() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Updated", "Description", true, null, null, null, null);
        User owner = new User(1L, "Owner", "owner@example.com");

        when(userService.getUserById(anyLong())).thenReturn(owner);
        when(itemMapper.toItem(any(), any())).thenReturn(new Item());
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenThrow(new ru.practicum.shareit.exception.ItemAccessDeniedException("User is not the owner"));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void searchItems_EmptyText_ShouldReturnEmptyList() throws Exception {
        when(itemService.searchItems("")).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", "")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}