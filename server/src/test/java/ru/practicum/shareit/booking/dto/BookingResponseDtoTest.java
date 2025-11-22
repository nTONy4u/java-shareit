package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingResponseDtoTest {

    @Test
    void bookingResponseDto_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new BookingResponseDto());
    }

    @Test
    void bookingResponseDto_AllArgsConstructor_ShouldSetFields() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        BookingResponseDto.BookerDto booker = new BookingResponseDto.BookerDto(1L, "Booker");
        BookingResponseDto.ItemDto item = new BookingResponseDto.ItemDto(1L, "Item");

        BookingResponseDto dto = new BookingResponseDto(1L, start, end, BookingStatus.WAITING, booker, item);

        assertEquals(1L, dto.getId());
        assertEquals(start, dto.getStart());
        assertEquals(end, dto.getEnd());
        assertEquals(BookingStatus.WAITING, dto.getStatus());
        assertEquals(booker, dto.getBooker());
        assertEquals(item, dto.getItem());
    }

    @Test
    void bookerDto_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new BookingResponseDto.BookerDto());
    }

    @Test
    void bookerDto_AllArgsConstructor_ShouldSetFields() {
        BookingResponseDto.BookerDto booker = new BookingResponseDto.BookerDto(1L, "Booker");

        assertEquals(1L, booker.getId());
        assertEquals("Booker", booker.getName());
    }

    @Test
    void itemDto_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new BookingResponseDto.ItemDto());
    }

    @Test
    void itemDto_AllArgsConstructor_ShouldSetFields() {
        BookingResponseDto.ItemDto item = new BookingResponseDto.ItemDto(1L, "Item");

        assertEquals(1L, item.getId());
        assertEquals("Item", item.getName());
    }
}