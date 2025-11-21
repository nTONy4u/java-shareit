package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {

    @Test
    void comment_EqualsAndHashCode_ShouldWorkCorrectly() {
        User author1 = new User(1L, "Author1", "author1@example.com");
        User author2 = new User(2L, "Author2", "author2@example.com");
        User owner = new User(3L, "Owner", "owner@example.com");
        Item item1 = new Item(1L, "Item1", "Desc1", true, owner, null);
        Item item2 = new Item(2L, "Item2", "Desc2", true, owner, null);
        LocalDateTime created = LocalDateTime.now();

        Comment comment1 = new Comment(1L, "Text1", item1, author1, created);
        Comment comment2 = new Comment(1L, "Text1", item1, author1, created);
        Comment comment3 = new Comment(2L, "Text2", item2, author2, created);

        // Проверка equals
        assertEquals(comment1, comment2);
        assertNotEquals(comment1, comment3);
        assertNotEquals(comment1, null);
        assertNotEquals(comment1, new Object());

        // Проверка hashCode
        assertEquals(comment1.hashCode(), comment2.hashCode());
        assertNotEquals(comment1.hashCode(), comment3.hashCode());

        // Проверка toString
        assertNotNull(comment1.toString());
        assertTrue(comment1.toString().contains("Comment"));
    }

    @Test
    void comment_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new Comment());
    }

    @Test
    void comment_SettersAndGetters_ShouldWork() {
        Comment comment = new Comment();
        User author = new User(1L, "Author", "author@example.com");
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Item", "Desc", true, owner, null);
        LocalDateTime created = LocalDateTime.now();

        comment.setId(1L);
        comment.setText("Text");
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);

        assertEquals(1L, comment.getId());
        assertEquals("Text", comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(author, comment.getAuthor());
        assertEquals(created, comment.getCreated());
    }
}