package ru.practicum.shareit.item.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class ItemMapper {
    private final BookingService bookingService;

    @Autowired
    public ItemMapper(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public ItemDto toItemDto(Item item) {
        return toItemDto(item, null);
    }

    public ItemDto toItemDto(Item item, Long userId) {
        ItemDto dto = new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                null,
                null,
                null   // nextBooking
        );

        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingService.getLastBookingForItem(item.getId());
            Booking nextBooking = bookingService.getNextBookingForItem(item.getId());

            if (lastBooking != null) {
                dto.setLastBooking(new ItemDto.BookingInfo(lastBooking.getId(), lastBooking.getBooker().getId()));
            }

            if (nextBooking != null) {
                dto.setNextBooking(new ItemDto.BookingInfo(nextBooking.getId(), nextBooking.getBooker().getId()));
            }
        }

        return dto;
    }

    public Item toItem(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        return item;
    }

    public Item toItem(ItemUpdateDto itemUpdateDto, User owner) {
        Item item = new Item();
        item.setName(itemUpdateDto.getName());
        item.setDescription(itemUpdateDto.getDescription());
        item.setAvailable(itemUpdateDto.getAvailable());
        item.setOwner(owner);
        return item;
    }
}