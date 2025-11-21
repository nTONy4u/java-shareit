package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingStatusTest {

    @Test
    void bookingStatus_Values_ShouldContainAllStatuses() {
        BookingStatus[] statuses = BookingStatus.values();

        assertEquals(4, statuses.length);
        assertArrayEquals(new BookingStatus[]{
                BookingStatus.WAITING,
                BookingStatus.APPROVED,
                BookingStatus.REJECTED,
                BookingStatus.CANCELED
        }, statuses);
    }

    @Test
    void bookingStatus_ValueOf_ShouldReturnCorrectStatus() {
        assertEquals(BookingStatus.WAITING, BookingStatus.valueOf("WAITING"));
        assertEquals(BookingStatus.APPROVED, BookingStatus.valueOf("APPROVED"));
        assertEquals(BookingStatus.REJECTED, BookingStatus.valueOf("REJECTED"));
        assertEquals(BookingStatus.CANCELED, BookingStatus.valueOf("CANCELED"));
    }
}