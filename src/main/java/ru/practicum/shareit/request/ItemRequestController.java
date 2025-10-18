package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> createItemRequest(
            @RequestBody ItemRequestCreateDto requestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating item request for user {}: {}", userId, requestDto);
        ItemRequest request = itemRequestService.createRequest(requestDto, userId);
        return ResponseEntity.ok(ItemRequestMapper.toDto(request));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getUserRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting requests for user {}", userId);
        List<ItemRequest> requests = itemRequestService.getUserRequests(userId);
        return ResponseEntity.ok(requests.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting all requests for user {}", userId);
        List<ItemRequest> requests = itemRequestService.getAllRequests(userId, from, size);
        return ResponseEntity.ok(requests.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(
            @PathVariable Long requestId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting request {} for user {}", requestId, userId);
        ItemRequest request = itemRequestService.getRequestById(requestId, userId);
        return ResponseEntity.ok(ItemRequestMapper.toDto(request));
    }
}