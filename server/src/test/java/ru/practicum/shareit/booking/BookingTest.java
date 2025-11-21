package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void booking_EqualsAndHashCode_ShouldWorkCorrectly() {
        User user1 = new User(1L, "User1", "user1@example.com");
        User user2 = new User(2L, "User2", "user2@example.com");
        Item item1 = new Item(1L, "Item1", "Desc1", true, user1, null);
        Item item2 = new Item(2L, "Item2", "Desc2", true, user2, null);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        Booking booking1 = new Booking(1L, start, end, item1, user1, BookingStatus.WAITING);
        Booking booking2 = new Booking(1L, start, end, item1, user1, BookingStatus.WAITING);
        Booking booking3 = new Booking(2L, start, end, item2, user2, BookingStatus.APPROVED);

        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
        assertNotEquals(booking1, null);
        assertNotEquals(booking1, new Object());

        assertEquals(booking1.hashCode(), booking2.hashCode());
        assertNotEquals(booking1.hashCode(), booking3.hashCode());

        assertNotNull(booking1.toString());
        assertTrue(booking1.toString().contains("Booking"));
    }

    @Test
    void booking_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new Booking());
    }

    @Test
    void booking_SettersAndGetters_ShouldWork() {
        Booking booking = new Booking();
        User user = new User(1L, "User", "user@example.com");
        Item item = new Item(1L, "Item", "Desc", true, user, null);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        assertEquals(1L, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(user, booking.getBooker());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }
}