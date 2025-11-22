package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
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
class BookingServiceIntegrationTest {

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
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner = userService.createUser(owner);

        booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        booker = userService.createUser(booker);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemService.createItem(item, owner.getId());
    }

    @Test
    void createBooking_ShouldCreateBooking() {
        BookingCreateDto bookingDto = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        Booking booking = bookingService.createBooking(bookingDto, booker.getId());

        assertNotNull(booking.getId());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
        assertEquals(item.getId(), booking.getItem().getId());
        assertEquals(booker.getId(), booking.getBooker().getId());
    }

    @Test
    void approveBooking_ShouldApproveBooking() {
        BookingCreateDto bookingDto = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        Booking booking = bookingService.createBooking(bookingDto, booker.getId());

        Booking approvedBooking = bookingService.approveBooking(booking.getId(), owner.getId(), true);

        assertEquals(BookingStatus.APPROVED, approvedBooking.getStatus());
    }

    @Test
    void getBookingById_ShouldReturnBooking() {
        BookingCreateDto bookingDto = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        Booking createdBooking = bookingService.createBooking(bookingDto, booker.getId());

        Booking foundBooking = bookingService.getBookingById(createdBooking.getId(), booker.getId());

        assertEquals(createdBooking.getId(), foundBooking.getId());
    }

    @Test
    void getUserBookings_ShouldReturnUserBookings() {
        BookingCreateDto bookingDto = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        bookingService.createBooking(bookingDto, booker.getId());

        List<Booking> bookings = bookingService.getUserBookings(booker.getId(), "ALL", 0, 10);

        assertFalse(bookings.isEmpty());
        assertEquals(booker.getId(), bookings.get(0).getBooker().getId());
    }

    @Test
    void getOwnerBookings_ShouldReturnOwnerBookings() {
        BookingCreateDto bookingDto = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        bookingService.createBooking(bookingDto, booker.getId());

        List<Booking> bookings = bookingService.getOwnerBookings(owner.getId(), "ALL", 0, 10);

        assertFalse(bookings.isEmpty());
        assertEquals(owner.getId(), bookings.get(0).getItem().getOwner().getId());
    }

    @Test
    void createBooking_WithUnavailableItem_ShouldThrowException() {
        item.setAvailable(false);
        itemService.updateItem(item.getId(), item, owner.getId());

        BookingCreateDto bookingDto = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto, booker.getId()));
    }

    @Test
    void getBookingById_NonExistingBooking_ShouldThrowException() {
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(999L, booker.getId()));
    }
}