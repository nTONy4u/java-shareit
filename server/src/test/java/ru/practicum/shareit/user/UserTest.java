package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void user_EqualsAndHashCode_ShouldWorkCorrectly() {
        User user1 = new User(1L, "User1", "user1@example.com");
        User user2 = new User(1L, "User1", "user1@example.com");
        User user3 = new User(2L, "User2", "user2@example.com");

        // Проверка equals
        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(user1, null);
        assertNotEquals(user1, new Object());

        // Проверка hashCode
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());

        // Проверка toString
        assertNotNull(user1.toString());
        assertTrue(user1.toString().contains("User"));
    }

    @Test
    void user_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new User());
    }

    @Test
    void user_SettersAndGetters_ShouldWork() {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");

        assertEquals(1L, user.getId());
        assertEquals("User", user.getName());
        assertEquals("user@example.com", user.getEmail());
    }
}