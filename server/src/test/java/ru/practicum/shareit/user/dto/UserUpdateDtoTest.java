package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserUpdateDtoTest {

    @Test
    void userUpdateDto_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new UserUpdateDto());
    }

    @Test
    void userUpdateDto_AllArgsConstructor_ShouldSetFields() {
        UserUpdateDto dto = new UserUpdateDto("Updated Name", "updated@example.com");

        assertEquals("Updated Name", dto.getName());
        assertEquals("updated@example.com", dto.getEmail());
    }

    @Test
    void userUpdateDto_SettersAndGetters_ShouldWorkCorrectly() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Name");
        dto.setEmail("email@example.com");

        assertEquals("Name", dto.getName());
        assertEquals("email@example.com", dto.getEmail());
    }

    @Test
    void userUpdateDto_EqualsAndHashCode_ShouldWorkCorrectly() {
        UserUpdateDto dto1 = new UserUpdateDto("Name", "email@example.com");
        UserUpdateDto dto2 = new UserUpdateDto("Name", "email@example.com");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void userUpdateDto_ToString_ShouldReturnString() {
        UserUpdateDto dto = new UserUpdateDto("Name", "email@example.com");
        assertNotNull(dto.toString());
        assertTrue(dto.toString().contains("Name"));
    }
}