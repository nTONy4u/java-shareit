package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequestDto toDto(ItemRequest request) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());

        if (request.getItems() != null) {
            dto.setItems(request.getItems().stream()
                    .map(ItemRequestMapper::toItemDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private static ItemRequestDto.ItemDto toItemDto(Item item) {
        ItemRequestDto.ItemDto dto = new ItemRequestDto.ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequest().getId());
        return dto;
    }
}