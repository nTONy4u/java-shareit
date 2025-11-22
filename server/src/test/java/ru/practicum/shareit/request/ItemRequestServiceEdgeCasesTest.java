package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceEdgeCasesTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemRequestService itemRequestService;

    @Test
    void createRequest_ValidRequest_ShouldCreate() {
        User requestor = new User(1L, "Requestor", "requestor@example.com");
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Need a drill");

        when(userService.getUserById(1L)).thenReturn(requestor);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenAnswer(invocation -> {
            ItemRequest request = invocation.getArgument(0);
            request.setId(1L);
            return request;
        });

        ItemRequest result = itemRequestService.createRequest(requestDto, 1L);

        assertNotNull(result);
        assertEquals("Need a drill", result.getDescription());
        assertEquals(requestor, result.getRequestor());
    }

    @Test
    void getUserRequests_WithItems_ShouldReturnRequestsWithItems() {
        User requestor = new User(1L, "Requestor", "requestor@example.com");
        ItemRequest request = new ItemRequest(1L, "Description", requestor, LocalDateTime.now(), null);
        Item item = new Item(1L, "Item", "Description", true, requestor, request);

        when(userService.getUserById(1L)).thenReturn(requestor);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L))
                .thenReturn(List.of(request));
        when(itemService.getItemsByRequestIds(List.of(1L)))
                .thenReturn(Map.of(1L, List.of(item)));

        List<ItemRequest> result = itemRequestService.getUserRequests(1L);

        assertFalse(result.isEmpty());
        assertNotNull(result.get(0).getItems());
        assertEquals(1, result.get(0).getItems().size());
    }

    @Test
    void getUserRequests_NoRequests_ShouldReturnEmptyList() {
        User requestor = new User(1L, "Requestor", "requestor@example.com");

        when(userService.getUserById(1L)).thenReturn(requestor);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(1L))
                .thenReturn(Collections.emptyList());

        List<ItemRequest> result = itemRequestService.getUserRequests(1L);

        assertTrue(result.isEmpty());

        verify(itemService, never()).getItemsByRequestIds(anyList());
    }

    @Test
    void getAllRequests_WithPagination_ShouldReturnRequests() {
        User user = new User(1L, "User", "user@example.com");
        User otherUser = new User(2L, "Other", "other@example.com");
        ItemRequest request = new ItemRequest(1L, "Description", otherUser, LocalDateTime.now(), null);

        when(userService.getUserById(1L)).thenReturn(user);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(eq(1L), any(Pageable.class)))
                .thenReturn(List.of(request));
        when(itemService.getItemsByRequestIds(List.of(1L)))
                .thenReturn(Map.of(1L, Collections.emptyList()));

        List<ItemRequest> result = itemRequestService.getAllRequests(1L, 0, 10);

        assertFalse(result.isEmpty());
    }

    @Test
    void getRequestById_WithItems_ShouldReturnRequestWithItems() {
        User user = new User(1L, "User", "user@example.com");
        User requestor = new User(2L, "Requestor", "requestor@example.com");
        ItemRequest request = new ItemRequest(1L, "Description", requestor, LocalDateTime.now(), null);
        Item item = new Item(1L, "Item", "Description", true, requestor, request);

        when(userService.getUserById(1L)).thenReturn(user);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemService.getItemsByRequestId(1L)).thenReturn(List.of(item));

        ItemRequest result = itemRequestService.getRequestById(1L, 1L);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void getRequestById_NotFound_ShouldThrowException() {
        User user = new User(1L, "User", "user@example.com");

        when(userService.getUserById(1L)).thenReturn(user);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 1L));
    }
}