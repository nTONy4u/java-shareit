package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser_ValidUser_ShouldCreateUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("Test User", createdUser.getName());
        assertEquals("test@example.com", createdUser.getEmail());
    }

    @Test
    void createUser_DuplicateEmail_ShouldThrowException() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("duplicate@example.com");
        userService.createUser(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("duplicate@example.com");

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(user2));
    }

    @Test
    void getUserById_ExistingUser_ShouldReturnUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        User createdUser = userService.createUser(user);

        User foundUser = userService.getUserById(createdUser.getId());

        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals("Test User", foundUser.getName());
    }

    @Test
    void getUserById_NonExistingUser_ShouldThrowException() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void updateUser_ValidUpdate_ShouldUpdateUser() {
        User user = new User();
        user.setName("Original Name");
        user.setEmail("original@example.com");
        User createdUser = userService.createUser(user);

        User updates = new User();
        updates.setName("Updated Name");
        updates.setEmail("updated@example.com");

        User updatedUser = userService.updateUser(createdUser.getId(), updates);

        assertEquals("Updated Name", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void getAllUsers_MultipleUsers_ShouldReturnAllUsers() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        userService.createUser(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        userService.createUser(user2);

        List<User> users = userService.getAllUsers();

        assertTrue(users.size() >= 2);
    }
}