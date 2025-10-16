package ru.practicum.shareit.user;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating user: {}", userDto);

        if (userService.isEmailExists(userDto.getEmail(), null)) {
            log.warn("Email already exists during creation: {}", userDto.getEmail());
            throw new EmailAlreadyExistsException("Email already exists: " + userDto.getEmail());
        }

        User user = UserMapper.toUser(userDto);
        User createdUser = userService.createUser(user);
        log.info("User created successfully: {}", createdUser.getId());
        return ResponseEntity.ok(UserMapper.toUserDto(createdUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        log.info("Getting user by id: {}", id);
        User user = userService.getUserById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });
        return ResponseEntity.ok(UserMapper.toUserDto(user));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Getting all users");
        List<UserDto> users = userService.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Found {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("Updating user with id: {}, data: {}", id, userUpdateDto);

        if (userUpdateDto.getEmail() != null && userService.isEmailExists(userUpdateDto.getEmail(), id)) {
            log.warn("Email already exists during update: {}", userUpdateDto.getEmail());
            throw new EmailAlreadyExistsException("Email already exists: " + userUpdateDto.getEmail());
        }

        User userUpdates = UserMapper.toUser(userUpdateDto);
        User updatedUser = userService.updateUser(id, userUpdates);
        log.info("User updated successfully: {}", id);
        return ResponseEntity.ok(UserMapper.toUserDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        userService.deleteUser(id);
        log.info("User deleted successfully: {}", id);
        return ResponseEntity.ok().build();
    }
}
