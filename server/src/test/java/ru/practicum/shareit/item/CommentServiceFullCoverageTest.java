package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceFullCoverageTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private ItemBookingInfoService itemBookingInfoService;

    @InjectMocks
    private CommentService commentService;

    @Test
    void createComment_WithEmptyText_ShouldCreateComment() {
        CommentCreateDto commentDto = new CommentCreateDto("");
        User author = new User(1L, "Author", "author@example.com");
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);

        when(userService.getUserById(1L)).thenReturn(author);
        when(itemService.getItemById(1L)).thenReturn(item);
        when(itemBookingInfoService.hasUserBookedItem(1L, 1L)).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(1L);
            return comment;
        });

        Comment result = commentService.createComment(commentDto, 1L, 1L);

        assertNotNull(result);
        assertEquals("", result.getText());
    }

    @Test
    void createComment_WithLongText_ShouldCreateComment() {
        String longText = "A".repeat(1000);
        CommentCreateDto commentDto = new CommentCreateDto(longText);
        User author = new User(1L, "Author", "author@example.com");
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);

        when(userService.getUserById(1L)).thenReturn(author);
        when(itemService.getItemById(1L)).thenReturn(item);
        when(itemBookingInfoService.hasUserBookedItem(1L, 1L)).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(1L);
            return comment;
        });

        Comment result = commentService.createComment(commentDto, 1L, 1L);

        assertNotNull(result);
        assertEquals(longText, result.getText());
    }

    @Test
    void createComment_UserHasNotBookedItem_ShouldThrowException() {
        CommentCreateDto commentDto = new CommentCreateDto("Test comment");
        User author = new User(1L, "Author", "author@example.com");
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);

        when(userService.getUserById(1L)).thenReturn(author);
        when(itemService.getItemById(1L)).thenReturn(item);
        when(itemBookingInfoService.hasUserBookedItem(1L, 1L)).thenReturn(false);

        assertThrows(ValidationException.class,
                () -> commentService.createComment(commentDto, 1L, 1L));
    }

    @Test
    void getCommentsByItemId_WithNonExistingItem_ShouldReturnEmptyList() {
        when(commentRepository.findByItemIdOrderByCreatedDesc(999L)).thenReturn(List.of());

        List<Comment> result = commentService.getCommentsByItemId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getCommentsByItemId_MultipleComments_ShouldReturnInCorrectOrder() {
        User author = new User(1L, "Author", "author@example.com");
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);

        Comment comment1 = new Comment(1L, "First comment", item, author,
                LocalDateTime.now().minusHours(2));
        Comment comment2 = new Comment(2L, "Second comment", item, author,
                LocalDateTime.now().minusHours(1));

        when(commentRepository.findByItemIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(comment2, comment1));

        List<Comment> result = commentService.getCommentsByItemId(1L);

        assertEquals(2, result.size());
        assertEquals("Second comment", result.get(0).getText());
        assertEquals("First comment", result.get(1).getText());
    }
}