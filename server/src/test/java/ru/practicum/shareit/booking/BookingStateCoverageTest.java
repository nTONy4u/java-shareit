package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class BookingStateCoverageTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = createUser("Owner", "owner@example.com");
        booker = createUser("Booker", "booker@example.com");
        item = createItem("Test Item", "Test Description", true, owner);
    }

    @Test
    void testAllBookingStates_Coverage() {
        // Создаем бронирования в разных статусах
        createBookingWithStatus(BookingStatus.WAITING);
        createBookingWithStatus(BookingStatus.APPROVED);
        createBookingWithStatus(BookingStatus.REJECTED);

        // Тестируем все возможные состояния
        testState("ALL");
        testState("CURRENT");
        testState("PAST");
        testState("FUTURE");
        testState("WAITING");
        testState("REJECTED");
        testState("CANCELED");

        // Тестируем неизвестное состояние
        assertThrows(Exception.class, () ->
                bookingService.getUserBookings(booker.getId(), "UNKNOWN_STATE", 0, 10));
    }

    private void testState(String state) {
        assertDoesNotThrow(() -> {
            List<Booking> userBookings = bookingService.getUserBookings(booker.getId(), state, 0, 10);
            List<Booking> ownerBookings = bookingService.getOwnerBookings(owner.getId(), state, 0, 10);
            assertNotNull(userBookings);
            assertNotNull(ownerBookings);
        });
    }

    private void createBookingWithStatus(BookingStatus status) {
        BookingCreateDto bookingDto = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        var booking = bookingService.createBooking(bookingDto, booker.getId());

        if (status != BookingStatus.WAITING) {
            bookingService.approveBooking(booking.getId(), owner.getId(), status == BookingStatus.APPROVED);
        }
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