package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemUpdateDtoTest {

    @Test
    void itemUpdateDto_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new ItemUpdateDto());
    }

    @Test
    void itemUpdateDto_AllArgsConstructor_ShouldSetFields() {
        ItemUpdateDto dto = new ItemUpdateDto("Updated Name", "Updated Description", false);

        assertEquals("Updated Name", dto.getName());
        assertEquals("Updated Description", dto.getDescription());
        assertFalse(dto.getAvailable());
    }

    @Test
    void itemUpdateDto_SettersAndGetters_ShouldWorkCorrectly() {
        ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("Name");
        dto.setDescription("Description");
        dto.setAvailable(true);

        assertEquals("Name", dto.getName());
        assertEquals("Description", dto.getDescription());
        assertTrue(dto.getAvailable());
    }

    @Test
    void itemUpdateDto_EqualsAndHashCode_ShouldWorkCorrectly() {
        ItemUpdateDto dto1 = new ItemUpdateDto("Name", "Desc", true);
        ItemUpdateDto dto2 = new ItemUpdateDto("Name", "Desc", true);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void itemUpdateDto_ToString_ShouldReturnString() {
        ItemUpdateDto dto = new ItemUpdateDto("Name", "Desc", true);
        assertNotNull(dto.toString());
        assertTrue(dto.toString().contains("Name"));
    }
}