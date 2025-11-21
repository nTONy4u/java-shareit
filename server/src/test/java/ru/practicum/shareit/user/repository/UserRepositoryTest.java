package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ExistingEmail_ShouldReturnUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        entityManager.persistAndFlush(user);

        Optional<User> result = userRepository.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
    }

    @Test
    void findByEmail_NonExistingEmail_ShouldReturnEmpty() {
        Optional<User> result = userRepository.findByEmail("nonexisting@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void save_ShouldPersistUser() {
        User user = new User();
        user.setName("New User");
        user.setEmail("new@example.com");

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("New User", saved.getName());
        assertEquals("new@example.com", saved.getEmail());
    }
}