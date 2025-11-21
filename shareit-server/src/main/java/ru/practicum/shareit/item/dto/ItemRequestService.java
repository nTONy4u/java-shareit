package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public ItemRequestService(ItemRequestRepository itemRequestRepository,
                              UserService userService, ItemService itemService) {
        this.itemRequestRepository = itemRequestRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Transactional
    public ItemRequest createRequest(ItemRequestCreateDto requestDto, Long requestorId) {
        User requestor = userService.getUserById(requestorId);

        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        ItemRequest savedRequest = itemRequestRepository.save(request);
        log.info("Item request created with id: {} by user: {}", savedRequest.getId(), requestorId);
        return savedRequest;
    }

    public List<ItemRequest> getUserRequests(Long requestorId) {
        userService.getUserById(requestorId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(requestorId);
        requests.forEach(request -> {
            List<Item> items = itemService.getItemsByRequestId(request.getId());
            request.setItems(items);
        });
        return requests;
    }

    public List<ItemRequest> getAllRequests(Long userId, int from, int size) {
        userService.getUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, pageable);
        requests.forEach(request -> {
            List<Item> items = itemService.getItemsByRequestId(request.getId());
            request.setItems(items);
        });
        return requests;
    }

    public ItemRequest getRequestById(Long requestId, Long userId) {
        userService.getUserById(userId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Item request not found"));

        List<Item> items = itemService.getItemsByRequestId(requestId);
        request.setItems(items);

        return request;
    }
}