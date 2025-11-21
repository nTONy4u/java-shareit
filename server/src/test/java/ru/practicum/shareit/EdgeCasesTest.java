package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class EdgeCasesTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingService bookingService;

    @Test
    void testBookingWithMinimalTimeDifference() {
        User owner = createUser("Owner", "owner@example.com");
        User booker = createUser("Booker", "booker@example.com");
        Item item = createItem("Test Item", "Test Description", true, owner);

        LocalDateTime start = LocalDateTime.now().plusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusMinutes(2);

        BookingCreateDto bookingDto = new BookingCreateDto(item.getId(), start, end);

        assertDoesNotThrow(() -> bookingService.createBooking(bookingDto, booker.getId()));
    }

    @Test
    void testBookingWithSameUserAsOwner() {
        User owner = createUser("Owner", "owner@example.com");
        Item item = createItem("Test Item", "Test Description", true, owner);

        BookingCreateDto bookingDto = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThrows(Exception.class, () -> bookingService.createBooking(bookingDto, owner.getId()));
    }

    @Test
    void testBookingWithUnavailableItem() {
        User owner = createUser("Owner", "owner@example.com");
        User booker = createUser("Booker", "booker@example.com");
        Item item = createItem("Unavailable Item", "Test Description", false, owner);

        BookingCreateDto bookingDto = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, booker.getId()));
    }

    @Test
    void testBookingWithEndBeforeStart() {
        User owner = createUser("Owner", "owner@example.com");
        User booker = createUser("Booker", "booker@example.com");
        Item item = createItem("Test Item", "Test Description", true, owner);

        BookingCreateDto bookingDto = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1)
        );

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, booker.getId()));
    }

    @Test
    void testBookingWithStartInPast() {
        User owner = createUser("Owner", "owner@example.com");
        User booker = createUser("Booker", "booker@example.com");
        Item item = createItem("Test Item", "Test Description", true, owner);

        BookingCreateDto bookingDto = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
        );

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingDto, booker.getId()));
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userService.createUser(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return itemService.createItem(item, owner.getId());
    }
}