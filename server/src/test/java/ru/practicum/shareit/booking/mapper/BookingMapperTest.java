package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    @Test
    void toResponseDto_ShouldMapCorrectly() {
        User owner = new User(1L, "Owner", "owner@example.com");
        User booker = new User(2L, "Booker", "booker@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingResponseDto dto = BookingMapper.toResponseDto(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getStatus(), dto.getStatus());
        assertEquals(booker.getId(), dto.getBooker().getId());
        assertEquals(booker.getName(), dto.getBooker().getName());
        assertEquals(item.getId(), dto.getItem().getId());
        assertEquals(item.getName(), dto.getItem().getName());
    }

    @Test
    void toResponseDto_WithNullBooking_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> BookingMapper.toResponseDto(null));
    }
}