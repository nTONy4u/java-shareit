package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void userDto_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new UserDto());
    }

    @Test
    void userDto_AllArgsConstructor_ShouldSetFields() {
        UserDto userDto = new UserDto(1L, "User", "user@example.com");

        assertEquals(1L, userDto.getId());
        assertEquals("User", userDto.getName());
        assertEquals("user@example.com", userDto.getEmail());
    }

    @Test
    void userDto_EqualsAndHashCode_ShouldWorkCorrectly() {
        UserDto dto1 = new UserDto(1L, "User", "user@example.com");
        UserDto dto2 = new UserDto(1L, "User", "user@example.com");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void userDto_ToString_ShouldReturnString() {
        UserDto dto = new UserDto();
        assertNotNull(dto.toString());
    }
}