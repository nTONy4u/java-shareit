package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Name cannot be blank");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email cannot be blank");
        }
        if (!isValidEmail(user.getEmail())) {
            throw new ValidationException("Email should be valid");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(Long id, User userUpdates) {
        User existingUser = getUserById(id);

        if (userUpdates.getEmail() != null) {
            if (userUpdates.getEmail().isBlank()) {
                throw new ValidationException("Email cannot be blank");
            }
            if (!isValidEmail(userUpdates.getEmail())) {
                throw new ValidationException("Email should be valid");
            }

            Optional<User> userWithSameEmail = userRepository.findByEmail(userUpdates.getEmail());
            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(id)) {
                throw new EmailAlreadyExistsException("Email already exists: " + userUpdates.getEmail());
            }
            existingUser.setEmail(userUpdates.getEmail());
        }

        if (userUpdates.getName() != null) {
            if (userUpdates.getName().isBlank()) {
                throw new ValidationException("Name cannot be blank");
            }
            existingUser.setName(userUpdates.getName());
        }

        if (userUpdates.getEmail() == null && userUpdates.getName() == null) {
            throw new ValidationException("At least one field must be provided for update");
        }

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private boolean isValidEmail(String email) {
        if (email == null) return true;
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}