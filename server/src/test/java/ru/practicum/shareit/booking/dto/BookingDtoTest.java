package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingDtoTest {

    @Test
    void bookingDto_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new BookingDto());
    }

    @Test
    void bookingDto_ToString_ShouldReturnString() {
        BookingDto dto = new BookingDto();
        assertNotNull(dto.toString());
    }

    @Test
    void bookingDto_HashCode_ShouldWork() {
        BookingDto dto1 = new BookingDto();
        BookingDto dto2 = new BookingDto();

        assertDoesNotThrow(() -> dto1.hashCode());
        assertDoesNotThrow(() -> dto2.hashCode());
    }

    @Test
    void bookingDto_Equals_ShouldWork() {
        BookingDto dto1 = new BookingDto();
        BookingDto dto2 = new BookingDto();

        assertNotEquals(dto1, dto2);

        assertEquals(dto1, dto1);

        assertNotEquals(null, dto1);

        assertNotEquals(dto1, new Object());
    }
}