package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    @Test
    void toDto_ShouldMapCorrectly() {
        User author = new User(1L, "Author", "author@example.com");
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Great item!");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        CommentDto dto = CommentMapper.toDto(comment);

        assertNotNull(dto);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getAuthor().getName(), dto.getAuthorName());
        assertEquals(comment.getCreated(), dto.getCreated());
    }

    @Test
    void toDto_WithNullComment_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> CommentMapper.toDto(null));
    }
}