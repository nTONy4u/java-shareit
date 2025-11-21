package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryEdgeCasesTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findByItemIdInOrderByCreatedDesc_WithMultipleItems_ShouldReturnComments() {
        User owner = createUser("Owner", "owner@example.com");
        User author = createUser("Author", "author@example.com");
        Item item1 = createItem("Item1", "Desc1", true, owner);
        Item item2 = createItem("Item2", "Desc2", true, owner);

        Comment comment1 = createComment("Comment1", item1, author);
        Comment comment2 = createComment("Comment2", item2, author);

        List<Comment> result = commentRepository.findByItemIdInOrderByCreatedDesc(
                List.of(item1.getId(), item2.getId()));

        assertEquals(2, result.size());
    }

    @Test
    void findByItemIdInOrderByCreatedDesc_WithNoItems_ShouldReturnEmpty() {
        List<Comment> result = commentRepository.findByItemIdInOrderByCreatedDesc(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void findByItemIdInOrderByCreatedDesc_WithNonExistingItems_ShouldReturnEmpty() {
        List<Comment> result = commentRepository.findByItemIdInOrderByCreatedDesc(List.of(999L, 1000L));
        assertTrue(result.isEmpty());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return entityManager.persistAndFlush(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return entityManager.persistAndFlush(item);
    }

    private Comment createComment(String text, Item item, User author) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return entityManager.persistAndFlush(comment);
    }
}