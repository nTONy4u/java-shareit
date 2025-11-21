package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private User author;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = createUser("Owner", "owner@example.com");
        author = createUser("Author", "author@example.com");
        item = createItem("Test Item", "Test Description", true, owner);
    }

    @Test
    void findByItemIdOrderByCreatedDesc_ShouldReturnCommentsForItem() {
        Comment comment1 = createComment("First comment", item, author);
        Comment comment2 = createComment("Second comment", item, author);

        List<Comment> result = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());

        assertEquals(2, result.size());
        assertEquals("Second comment", result.get(0).getText());
        assertEquals("First comment", result.get(1).getText());
    }

    @Test
    void findByItemIdInOrderByCreatedDesc_ShouldReturnCommentsForMultipleItems() {
        Item item2 = createItem("Second Item", "Description", true, owner);
        Comment comment1 = createComment("Comment for item1", item, author);
        Comment comment2 = createComment("Comment for item2", item2, author);

        List<Comment> result = commentRepository.findByItemIdInOrderByCreatedDesc(
                List.of(item.getId(), item2.getId()));

        assertEquals(2, result.size());
    }

    @Test
    void findByItemIdOrderByCreatedDesc_NoComments_ShouldReturnEmptyList() {
        List<Comment> result = commentRepository.findByItemIdOrderByCreatedDesc(999L);
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