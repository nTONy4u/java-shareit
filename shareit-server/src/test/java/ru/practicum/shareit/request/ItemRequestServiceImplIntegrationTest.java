package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    private User requestor;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setName("Requestor");
        requestor.setEmail("requestor@example.com");
        requestor = userService.createUser(requestor);
    }

    @Test
    void createRequest_ValidRequest_ShouldCreateRequest() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Need a drill for home repairs");

        ItemRequest request = itemRequestService.createRequest(requestDto, requestor.getId());

        assertNotNull(request.getId());
        assertEquals("Need a drill for home repairs", request.getDescription());
        assertEquals(requestor.getId(), request.getRequestor().getId());
    }

    @Test
    void getUserRequests_UserWithRequests_ShouldReturnRequests() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Test request");
        itemRequestService.createRequest(requestDto, requestor.getId());

        List<ItemRequest> requests = itemRequestService.getUserRequests(requestor.getId());

        assertFalse(requests.isEmpty());
        assertEquals(requestor.getId(), requests.get(0).getRequestor().getId());
    }

    @Test
    void getAllRequests_MultipleUsers_ShouldReturnOtherUsersRequests() {
        User anotherUser = new User();
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");
        anotherUser = userService.createUser(anotherUser);

        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Other user request");
        itemRequestService.createRequest(requestDto, anotherUser.getId());

        List<ItemRequest> requests = itemRequestService.getAllRequests(requestor.getId(), 0, 10);

        assertFalse(requests.isEmpty());
        assertEquals(anotherUser.getId(), requests.get(0).getRequestor().getId());
    }

    @Test
    void getRequestById_ExistingRequest_ShouldReturnRequest() {
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Specific request");
        ItemRequest createdRequest = itemRequestService.createRequest(requestDto, requestor.getId());

        ItemRequest foundRequest = itemRequestService.getRequestById(createdRequest.getId(), requestor.getId());

        assertEquals(createdRequest.getId(), foundRequest.getId());
        assertEquals("Specific request", foundRequest.getDescription());
    }

    @Test
    void getRequestById_NonExistingRequest_ShouldThrowException() {
        assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getRequestById(999L, requestor.getId()));
    }
}