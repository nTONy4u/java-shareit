package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final Set<String> emails = Collections.synchronizedSet(new HashSet<>());
    private Long idCounter = 1L;

    public User save(User user) {
        log.debug("Saving user: {}", user);

        if (user.getId() == null) {
            if (emails.contains(user.getEmail())) {
                log.error("Email already exists during save: {}", user.getEmail());
                throw new EmailAlreadyExistsException("Email already exists: " + user.getEmail());
            }
            user.setId(idCounter++);
            users.put(user.getId(), user);
            emails.add(user.getEmail());
            log.info("New user saved with id: {}", user.getId());
        } else {
            User existingUser = users.get(user.getId());
            if (existingUser != null) {
                if (!existingUser.getEmail().equals(user.getEmail()) && emails.contains(user.getEmail())) {
                    log.error("Email already exists during update: {}", user.getEmail());
                    throw new EmailAlreadyExistsException("Email already exists: " + user.getEmail());
                }
                if (!existingUser.getEmail().equals(user.getEmail())) {
                    emails.remove(existingUser.getEmail());
                    emails.add(user.getEmail());
                    log.debug("Email updated for user {}: {} -> {}", user.getId(), existingUser.getEmail(), user.getEmail());
                }
                users.put(user.getId(), user);
                log.info("User updated: {}", user.getId());
            }
        }
        return user;
    }

    public Optional<User> findById(Long id) {
        log.debug("Finding user by id: {}", id);
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        log.debug("Finding all users");
        return new ArrayList<>(users.values());
    }

    public void deleteById(Long id) {
        log.debug("Deleting user by id: {}", id);
        User user = users.get(id);
        if (user != null) {
            emails.remove(user.getEmail());
            users.remove(id);
            log.info("User deleted: {}", id);
        } else {
            log.warn("User not found for deletion: {}", id);
        }
    }

    public boolean emailExists(String email, Long excludeUserId) {
        log.debug("Checking email existence: {}, excludeUserId: {}", email, excludeUserId);
        boolean exists = users.values().stream()
                .filter(user -> excludeUserId == null || !user.getId().equals(excludeUserId))
                .anyMatch(user -> user.getEmail().equals(email));
        log.debug("Email {} exists: {}", email, exists);
        return exists;
    }
}