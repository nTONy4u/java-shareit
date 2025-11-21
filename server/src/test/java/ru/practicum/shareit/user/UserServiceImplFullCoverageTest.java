package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplFullCoverageTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_WithEmailContainingSpaces_ShouldThrowException() {
        User user = new User();
        user.setName("Valid Name");
        user.setEmail("  test@example.com  ");

        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    void updateUser_WithEmailContainingSpaces_ShouldThrowException() {
        User existingUser = new User(1L, "Old Name", "old@example.com");
        User updates = new User();
        updates.setEmail("  new@example.com  ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThrows(ValidationException.class, () -> userService.updateUser(1L, updates));
    }

    @Test
    void updateUser_WithSameEmailButDifferentCase_ShouldUpdateSuccessfully() {
        User existingUser = new User(1L, "Old Name", "old@example.com");
        User updates = new User();
        updates.setEmail("OLD@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("OLD@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User result = userService.updateUser(1L, updates);

        assertNotNull(result);
        verify(userRepository).save(existingUser);
    }

    @Test
    void getAllUsers_WhenRepositoryReturnsEmpty_ShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    void createUser_WithMaxLengthEmail_ShouldCreateUser() {
        String longEmail = "a".repeat(500) + "@example.com";
        User user = new User();
        user.setName("Test User");
        user.setEmail(longEmail);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals(longEmail, result.getEmail());
    }

    @Test
    void createUser_WithComplexEmail_ShouldCreateUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("user.name+tag@example.co.uk");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(user);

        assertNotNull(result);
        assertEquals("user.name+tag@example.co.uk", result.getEmail());
    }

    @Test
    void updateUser_WithComplexEmail_ShouldUpdateUser() {
        User existingUser = new User(1L, "Old Name", "old@example.com");
        User updates = new User();
        updates.setEmail("user.name+tag@example.co.uk");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("user.name+tag@example.co.uk")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User result = userService.updateUser(1L, updates);

        assertNotNull(result);
        verify(userRepository).save(existingUser);
    }

    @Test
    void createUser_WithInvalidEmailFormat_ShouldThrowException() {
        User user = new User();
        user.setName("Valid Name");
        user.setEmail("invalid-email@");

        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    void updateUser_WithInvalidEmailFormat_ShouldThrowException() {
        User existingUser = new User(1L, "Old Name", "old@example.com");
        User updates = new User();
        updates.setEmail("invalid-email@");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThrows(ValidationException.class, () -> userService.updateUser(1L, updates));
    }

    @Test
    void createUser_WithEmailMissingDomain_ShouldThrowException() {
        User user = new User();
        user.setName("Valid Name");
        user.setEmail("user@");

        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    void updateUser_WithEmailMissingDomain_ShouldThrowException() {
        User existingUser = new User(1L, "Old Name", "old@example.com");
        User updates = new User();
        updates.setEmail("user@");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThrows(ValidationException.class, () -> userService.updateUser(1L, updates));
    }

    @Test
    void createUser_WithEmailMissingAtSymbol_ShouldThrowException() {
        User user = new User();
        user.setName("Valid Name");
        user.setEmail("userexample.com");

        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    void updateUser_WithEmailMissingAtSymbol_ShouldThrowException() {
        User existingUser = new User(1L, "Old Name", "old@example.com");
        User updates = new User();
        updates.setEmail("userexample.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        assertThrows(ValidationException.class, () -> userService.updateUser(1L, updates));
    }
}