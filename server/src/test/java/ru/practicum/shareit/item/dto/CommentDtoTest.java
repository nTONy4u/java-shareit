package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentDtoTest {

    @Test
    void commentDto_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new CommentDto());
    }

    @Test
    void commentDto_AllArgsConstructor_ShouldSetFields() {
        LocalDateTime created = LocalDateTime.now();
        CommentDto dto = new CommentDto(1L, "Text", "Author", created);

        assertEquals(1L, dto.getId());
        assertEquals("Text", dto.getText());
        assertEquals("Author", dto.getAuthorName());
        assertEquals(created, dto.getCreated());
    }

    @Test
    void commentDto_EqualsAndHashCode_ShouldWorkCorrectly() {
        LocalDateTime created = LocalDateTime.now();
        CommentDto dto1 = new CommentDto(1L, "Text", "Author", created);
        CommentDto dto2 = new CommentDto(1L, "Text", "Author", created);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void commentDto_ToString_ShouldReturnString() {
        CommentDto dto = new CommentDto();
        assertNotNull(dto.toString());
    }
}