package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerFullCoverageTest {

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
    void createItem_WithRequestId_ShouldSetRequest() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Drill", "Powerful drill", true, 1L, null, null, null);
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        ItemRequest request = new ItemRequest(1L, "Need drill", owner, LocalDateTime.now(), null);
        ItemDto responseDto = new ItemDto(1L, "Drill", "Powerful drill", true, 1L, null, null, null);

        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemMapper.toItem(any(ItemDto.class), any(User.class))).thenReturn(item);
        when(itemRequestService.getRequestById(1L, 1L)).thenReturn(request);
        when(itemService.createItem(any(Item.class), anyLong())).thenReturn(item);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.requestId").value(1));

        verify(itemRequestService).getRequestById(1L, 1L);
    }

    @Test
    void createItem_WithoutRequestId_ShouldNotSetRequest() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Drill", "Powerful drill", true, null, null, null, null);
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        ItemDto responseDto = new ItemDto(1L, "Drill", "Powerful drill", true, null, null, null, null);

        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemMapper.toItem(any(ItemDto.class), any(User.class))).thenReturn(item);
        when(itemService.createItem(any(Item.class), anyLong())).thenReturn(item);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());

        verify(itemRequestService, never()).getRequestById(anyLong(), anyLong());
    }

    @Test
    void getItem_OwnerWithBookings_ShouldReturnItemWithBookings() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        User booker = new User(2L, "Booker", "booker@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        List<Comment> comments = List.of();

        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);
        Booking nextBooking = new Booking(2L, LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3), item, booker, BookingStatus.APPROVED);

        ItemDto itemDto = new ItemDto(1L, "Drill", "Powerful drill", true, null,
                List.of(), new ItemDto.BookingInfo(1L, 2L), new ItemDto.BookingInfo(2L, 2L));

        when(itemService.getItemById(1L)).thenReturn(item);
        when(commentService.getCommentsByItemId(1L)).thenReturn(comments);
        when(itemBookingInfoService.getLastBookingForItem(1L)).thenReturn(lastBooking);
        when(itemBookingInfoService.getNextBookingForItem(1L)).thenReturn(nextBooking);
        when(itemMapper.toItemDto(any(Item.class), any(), any())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastBooking.id").value(1))
                .andExpect(jsonPath("$.nextBooking.id").value(2));

        verify(itemBookingInfoService).getLastBookingForItem(1L);
        verify(itemBookingInfoService).getNextBookingForItem(1L);
    }

    @Test
    void getItem_OwnerWithOnlyLastBooking_ShouldReturnItemWithLastBooking() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        User booker = new User(2L, "Booker", "booker@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        List<Comment> comments = List.of();

        Booking lastBooking = new Booking(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);

        ItemDto itemDto = new ItemDto(1L, "Drill", "Powerful drill", true, null,
                List.of(), new ItemDto.BookingInfo(1L, 2L), null);

        when(itemService.getItemById(1L)).thenReturn(item);
        when(commentService.getCommentsByItemId(1L)).thenReturn(comments);
        when(itemBookingInfoService.getLastBookingForItem(1L)).thenReturn(lastBooking);
        when(itemBookingInfoService.getNextBookingForItem(1L)).thenReturn(null);
        when(itemMapper.toItemDto(any(Item.class), any(), any())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastBooking.id").value(1))
                .andExpect(jsonPath("$.nextBooking").doesNotExist());
    }

    @Test
    void getItem_OwnerWithOnlyNextBooking_ShouldReturnItemWithNextBooking() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        User booker = new User(2L, "Booker", "booker@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        List<Comment> comments = List.of();

        Booking nextBooking = new Booking(2L, LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3), item, booker, BookingStatus.APPROVED);

        ItemDto itemDto = new ItemDto(1L, "Drill", "Powerful drill", true, null,
                List.of(), null, new ItemDto.BookingInfo(2L, 2L));

        when(itemService.getItemById(1L)).thenReturn(item);
        when(commentService.getCommentsByItemId(1L)).thenReturn(comments);
        when(itemBookingInfoService.getLastBookingForItem(1L)).thenReturn(null);
        when(itemBookingInfoService.getNextBookingForItem(1L)).thenReturn(nextBooking);
        when(itemMapper.toItemDto(any(Item.class), any(), any())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastBooking").doesNotExist())
                .andExpect(jsonPath("$.nextBooking.id").value(2));
    }

    @Test
    void getItem_OwnerWithoutBookings_ShouldReturnItemWithoutBookings() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        List<Comment> comments = List.of();

        ItemDto itemDto = new ItemDto(1L, "Drill", "Powerful drill", true, null, List.of(), null, null);

        when(itemService.getItemById(1L)).thenReturn(item);
        when(commentService.getCommentsByItemId(1L)).thenReturn(comments);
        when(itemBookingInfoService.getLastBookingForItem(1L)).thenReturn(null);
        when(itemBookingInfoService.getNextBookingForItem(1L)).thenReturn(null);
        when(itemMapper.toItemDto(any(Item.class), any(), any())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastBooking").doesNotExist())
                .andExpect(jsonPath("$.nextBooking").doesNotExist());

        verify(itemBookingInfoService).getLastBookingForItem(1L);
        verify(itemBookingInfoService).getNextBookingForItem(1L);
    }

    @Test
    void getItem_NotOwner_ShouldReturnItemWithoutBookings() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        User otherUser = new User(2L, "Other", "other@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        List<Comment> comments = List.of(
                new Comment(1L, "Great item!", item, otherUser, LocalDateTime.now())
        );

        ItemDto itemDto = new ItemDto(1L, "Drill", "Powerful drill", true, null,
                List.of(new CommentDto(1L, "Great item!", "Other", LocalDateTime.now())), null, null);

        when(itemService.getItemById(1L)).thenReturn(item);
        when(commentService.getCommentsByItemId(1L)).thenReturn(comments);
        when(itemMapper.toItemDto(any(Item.class), any(), any())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 3L)) // Different user
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastBooking").doesNotExist())
                .andExpect(jsonPath("$.nextBooking").doesNotExist())
                .andExpect(jsonPath("$.comments[0].text").value("Great item!"));

        verify(itemBookingInfoService, never()).getLastBookingForItem(anyLong());
        verify(itemBookingInfoService, never()).getNextBookingForItem(anyLong());
    }

    @Test
    void getItem_WithComments_ShouldReturnItemWithComments() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        User author = new User(2L, "Author", "author@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        List<Comment> comments = List.of(
                new Comment(1L, "Great item!", item, author, LocalDateTime.now()),
                new Comment(2L, "Very useful", item, author, LocalDateTime.now().plusHours(1))
        );

        List<CommentDto> commentDtos = List.of(
                new CommentDto(1L, "Great item!", "Author", LocalDateTime.now()),
                new CommentDto(2L, "Very useful", "Author", LocalDateTime.now().plusHours(1))
        );

        ItemDto itemDto = new ItemDto(1L, "Drill", "Powerful drill", true, null, commentDtos, null, null);

        when(itemService.getItemById(1L)).thenReturn(item);
        when(commentService.getCommentsByItemId(1L)).thenReturn(comments);
        when(itemMapper.toItemDto(any(Item.class), any(), any())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments.length()").value(2))
                .andExpect(jsonPath("$.comments[0].text").value("Great item!"))
                .andExpect(jsonPath("$.comments[1].text").value("Very useful"));
    }

    @Test
    void getItemsByOwner_WithBookings_ShouldReturnItemsWithBookings() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        User booker = new User(2L, "Booker", "booker@example.com");

        Item item1 = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        Item item2 = new Item(2L, "Hammer", "Heavy hammer", true, owner, null);

        List<Item> items = List.of(item1, item2);

        Booking lastBooking1 = new Booking(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), item1, booker, BookingStatus.APPROVED);
        Booking nextBooking2 = new Booking(2L, LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(3), item2, booker, BookingStatus.APPROVED);

        ItemDto itemDto1 = new ItemDto(1L, "Drill", "Powerful drill", true, null,
                List.of(), new ItemDto.BookingInfo(1L, 2L), null);
        ItemDto itemDto2 = new ItemDto(2L, "Hammer", "Heavy hammer", true, null,
                List.of(), null, new ItemDto.BookingInfo(2L, 2L));

        when(itemService.getItemsByOwner(1L)).thenReturn(items);
        when(itemBookingInfoService.getLastBookingForItem(1L)).thenReturn(lastBooking1);
        when(itemBookingInfoService.getNextBookingForItem(1L)).thenReturn(null);
        when(itemBookingInfoService.getLastBookingForItem(2L)).thenReturn(null);
        when(itemBookingInfoService.getNextBookingForItem(2L)).thenReturn(nextBooking2);

        when(itemMapper.toItemDto(eq(item1), any(), any())).thenReturn(itemDto1);
        when(itemMapper.toItemDto(eq(item2), any(), any())).thenReturn(itemDto2);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastBooking.id").value(1))
                .andExpect(jsonPath("$[1].nextBooking.id").value(2));

        verify(itemBookingInfoService).getLastBookingForItem(1L);
        verify(itemBookingInfoService).getNextBookingForItem(1L);
        verify(itemBookingInfoService).getLastBookingForItem(2L);
        verify(itemBookingInfoService).getNextBookingForItem(2L);
    }

    @Test
    void getItemsByOwner_WithoutBookings_ShouldReturnItemsWithoutBookings() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        List<Item> items = List.of(item);

        ItemDto itemDto = new ItemDto(1L, "Drill", "Powerful drill", true, null, List.of(), null, null);

        when(itemService.getItemsByOwner(1L)).thenReturn(items);
        when(itemBookingInfoService.getLastBookingForItem(1L)).thenReturn(null);
        when(itemBookingInfoService.getNextBookingForItem(1L)).thenReturn(null);
        when(itemMapper.toItemDto(any(Item.class), any(), any())).thenReturn(itemDto);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastBooking").doesNotExist())
                .andExpect(jsonPath("$[0].nextBooking").doesNotExist());
    }

    @Test
    void getItemsByOwner_WithMultipleItems_ShouldReturnAllItems() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");

        Item item1 = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        Item item2 = new Item(2L, "Hammer", "Heavy hammer", true, owner, null);
        Item item3 = new Item(3L, "Saw", "Electric saw", true, owner, null);

        List<Item> items = List.of(item1, item2, item3);

        ItemDto itemDto1 = new ItemDto(1L, "Drill", "Powerful drill", true, null, List.of(), null, null);
        ItemDto itemDto2 = new ItemDto(2L, "Hammer", "Heavy hammer", true, null, List.of(), null, null);
        ItemDto itemDto3 = new ItemDto(3L, "Saw", "Electric saw", true, null, List.of(), null, null);

        when(itemService.getItemsByOwner(1L)).thenReturn(items);
        when(itemBookingInfoService.getLastBookingForItem(anyLong())).thenReturn(null);
        when(itemBookingInfoService.getNextBookingForItem(anyLong())).thenReturn(null);

        when(itemMapper.toItemDto(eq(item1), any(), any())).thenReturn(itemDto1);
        when(itemMapper.toItemDto(eq(item2), any(), any())).thenReturn(itemDto2);
        when(itemMapper.toItemDto(eq(item3), any(), any())).thenReturn(itemDto3);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Drill"))
                .andExpect(jsonPath("$[1].name").value("Hammer"))
                .andExpect(jsonPath("$[2].name").value("Saw"));
    }

    @Test
    void searchItems_WithResults_ShouldReturnItems() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Drill", "Powerful electric drill", true, owner, null);
        List<Item> items = List.of(item);

        ItemDto itemDto = new ItemDto(1L, "Drill", "Powerful electric drill", true, null, null, null, null);

        when(itemService.searchItems("drill")).thenReturn(items);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);

        mockMvc.perform(get("/items/search")
                        .param("text", "drill")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void searchItems_NoResults_ShouldReturnEmptyList() throws Exception {
        when(itemService.searchItems("nonexistent")).thenReturn(List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", "nonexistent")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
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

    @Test
    void searchItems_MultipleResults_ShouldReturnAllMatchingItems() throws Exception {
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item1 = new Item(1L, "Electric Drill", "Powerful drill", true, owner, null);
        Item item2 = new Item(2L, "Cordless Drill", "Battery powered", true, owner, null);
        List<Item> items = List.of(item1, item2);

        ItemDto itemDto1 = new ItemDto(1L, "Electric Drill", "Powerful drill", true, null, null, null, null);
        ItemDto itemDto2 = new ItemDto(2L, "Cordless Drill", "Battery powered", true, null, null, null, null);

        when(itemService.searchItems("drill")).thenReturn(items);
        when(itemMapper.toItemDto(item1)).thenReturn(itemDto1);
        when(itemMapper.toItemDto(item2)).thenReturn(itemDto2);

        mockMvc.perform(get("/items/search")
                        .param("text", "drill")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Electric Drill"))
                .andExpect(jsonPath("$[1].name").value("Cordless Drill"));
    }

    @Test
    void addComment_ShouldReturnComment() throws Exception {
        CommentCreateDto commentDto = new CommentCreateDto("Great item!");
        User author = new User(1L, "Author", "author@example.com");
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Drill", "Powerful drill", true, owner, null);
        Comment comment = new Comment(1L, "Great item!", item, author, LocalDateTime.now());

        when(commentService.createComment(any(CommentCreateDto.class), eq(1L), eq(1L)))
                .thenReturn(comment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());

        verify(commentService).createComment(any(CommentCreateDto.class), eq(1L), eq(1L));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Updated Drill", "Updated description", false, null, null, null, null);
        User owner = new User(1L, "Owner", "owner@example.com");
        Item updatedItem = new Item(1L, "Updated Drill", "Updated description", false, owner, null);
        ItemDto responseDto = new ItemDto(1L, "Updated Drill", "Updated description", false, null, null, null, null);

        when(userService.getUserById(1L)).thenReturn(owner);
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
    void updateItem_PartialUpdate_ShouldReturnPartiallyUpdatedItem() throws Exception {
        // Update only name
        ItemDto itemDto = new ItemDto(null, "Updated Name Only", null, null, null, null, null, null);
        User owner = new User(1L, "Owner", "owner@example.com");
        Item originalItem = new Item(1L, "Original", "Original Description", true, owner, null);
        Item updatedItem = new Item(1L, "Updated Name Only", "Original Description", true, owner, null);
        ItemDto responseDto = new ItemDto(1L, "Updated Name Only", "Original Description", true, null, null, null, null);

        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemMapper.toItem(any(ItemDto.class), any(User.class))).thenReturn(updatedItem);
        when(itemService.updateItem(anyLong(), any(Item.class), anyLong())).thenReturn(updatedItem);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name Only"))
                .andExpect(jsonPath("$.description").value("Original Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void updateItem_UpdateOnlyAvailable_ShouldReturnUpdatedItem() throws Exception {
        // Update only available status
        ItemDto itemDto = new ItemDto(null, null, null, false, null, null, null, null);
        User owner = new User(1L, "Owner", "owner@example.com");
        Item originalItem = new Item(1L, "Original", "Original Description", true, owner, null);
        Item updatedItem = new Item(1L, "Original", "Original Description", false, owner, null);
        ItemDto responseDto = new ItemDto(1L, "Original", "Original Description", false, null, null, null, null);

        when(userService.getUserById(1L)).thenReturn(owner);
        when(itemMapper.toItem(any(ItemDto.class), any(User.class))).thenReturn(updatedItem);
        when(itemService.updateItem(anyLong(), any(Item.class), anyLong())).thenReturn(updatedItem);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Original"))
                .andExpect(jsonPath("$.description").value("Original Description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void getItem_ItemWithNullOwner_ShouldHandleGracefully() throws Exception {
        Item item = new Item(1L, "Drill", "Powerful drill", true, null, null);
        List<Comment> comments = List.of();

        ItemDto itemDto = new ItemDto(1L, "Drill", "Powerful drill", true, null, List.of(), null, null);

        when(itemService.getItemById(1L)).thenReturn(item);
        when(commentService.getCommentsByItemId(1L)).thenReturn(comments);
        when(itemMapper.toItemDto(any(Item.class), any(), any())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Drill"));

        // Should not try to get bookings if owner is null
        verify(itemBookingInfoService, never()).getLastBookingForItem(anyLong());
        verify(itemBookingInfoService, never()).getNextBookingForItem(anyLong());
    }
}