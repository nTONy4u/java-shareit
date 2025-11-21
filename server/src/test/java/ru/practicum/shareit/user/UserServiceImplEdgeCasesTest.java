package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplEdgeCasesTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_WithNullName_ShouldThrowException() {
        User user = new User();
        user.setName(null);
        user.setEmail("test@example.com");

        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_WithBlankName_ShouldThrowException() {
        User user = new User();
        user.setName("   ");
        user.setEmail("test@example.com");

        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_WithNullEmail_ShouldThrowException() {
        User user = new User();
        user.setName("Test User");
        user.setEmail(null);

        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    void createUser_WithBlankEmail_ShouldThrowException() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("   ");

        assertThrows(ValidationException.class, () -> userService.createUser(user));
    }

    @Test
    void updateUser_WithDuplicateEmail_ShouldThrowException() {
        User existingUser = new User(1L, "Old Name", "old@example.com");
        User otherUser = new User(2L, "Other User", "new@example.com");
        User updates = new User();
        updates.setEmail("new@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.of(otherUser));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(1L, updates));
    }

    @Test
    void getUserById_NotFound_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void deleteUser_ShouldCallRepository() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}