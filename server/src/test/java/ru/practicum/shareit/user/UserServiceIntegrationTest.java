package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user = userService.createUser(user);
    }

    @Test
    void createUser_ShouldCreateUser() {
        assertNotNull(user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void getUserById_ShouldReturnUser() {
        User foundUser = userService.getUserById(user.getId());

        assertEquals(user.getId(), foundUser.getId());
        assertEquals("Test User", foundUser.getName());
        assertEquals("test@example.com", foundUser.getEmail());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        userService.createUser(user2);

        List<User> users = userService.getAllUsers();

        assertTrue(users.size() >= 2);
    }

    @Test
    void updateUser_ShouldUpdateUser() {
        User updates = new User();
        updates.setName("Updated Name");
        updates.setEmail("updated@example.com");

        User updatedUser = userService.updateUser(user.getId(), updates);

        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldThrowException() {
        User duplicate = new User();
        duplicate.setName("Duplicate User");
        duplicate.setEmail("test@example.com");

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(duplicate));
    }

    @Test
    void getUserById_NonExistingUser_ShouldThrowException() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }
}