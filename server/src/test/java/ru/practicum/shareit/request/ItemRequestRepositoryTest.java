package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private User requestor;
    private User otherUser;

    @BeforeEach
    void setUp() {
        requestor = createUser("Requestor", "requestor@example.com");
        otherUser = createUser("Other User", "other@example.com");
    }

    @Test
    void findByRequestorIdOrderByCreatedDesc_ShouldReturnUserRequests() {
        createRequest("First request", requestor);
        createRequest("Second request", requestor);

        List<ItemRequest> result = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestor.getId());

        assertEquals(2, result.size());
        assertEquals("Second request", result.get(0).getDescription());
        assertEquals("First request", result.get(1).getDescription());
    }

    @Test
    void findByRequestorIdNotOrderByCreatedDesc_ShouldReturnOtherUsersRequests() {
        createRequest("Requestor's request", requestor);
        createRequest("Other user's request", otherUser);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("created").descending());
        List<ItemRequest> result = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(
                requestor.getId(), pageable);

        assertEquals(1, result.size());
        assertEquals("Other user's request", result.get(0).getDescription());
    }

    @Test
    void findByRequestorIdNotOrderByCreatedDesc_WithPagination_ShouldReturnPaginatedResults() {
        for (int i = 1; i <= 3; i++) {
            createRequest("Request " + i, otherUser);
        }

        Pageable pageable = PageRequest.of(0, 2, Sort.by("created").descending());
        List<ItemRequest> result = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(
                requestor.getId(), pageable);

        assertEquals(2, result.size());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return entityManager.persistAndFlush(user);
    }

    private ItemRequest createRequest(String description, User requestor) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return entityManager.persistAndFlush(request);
    }
}