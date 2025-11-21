package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

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
    void findCurrentBookingsByBooker_ShouldReturnCurrentBookings() {
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                BookingStatus.APPROVED);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());
        List<Booking> result = bookingRepository.findCurrentBookingsByBooker(
                booker.getId(), LocalDateTime.now(), pageable);

        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void findCurrentBookingsByOwner_ShouldReturnCurrentBookings() {
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                BookingStatus.APPROVED);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());
        List<Booking> result = bookingRepository.findCurrentBookingsByOwner(
                owner.getId(), LocalDateTime.now(), pageable);

        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void findCompletedBookingsByBookerAndItem_ShouldReturnCompletedBookings() {
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED);

        List<Booking> result = bookingRepository.findCompletedBookingsByBookerAndItem(
                booker.getId(), item.getId(), LocalDateTime.now(), BookingStatus.APPROVED);

        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void findWaitingBookingByIdAndBooker_ShouldReturnWaitingBooking() {
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        Optional<Booking> result = bookingRepository.findWaitingBookingByIdAndBooker(
                booking.getId(), booker.getId());

        assertTrue(result.isPresent());
        assertEquals(booking.getId(), result.get().getId());
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByStartDesc_ShouldReturnPastBookings() {
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                BookingStatus.APPROVED);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());
        List<Booking> result = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                booker.getId(), LocalDateTime.now(), pageable);

        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void findByBookerIdAndStartAfterOrderByStartDesc_ShouldReturnFutureBookings() {
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.APPROVED);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());
        List<Booking> result = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                booker.getId(), LocalDateTime.now(), pageable);

        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDesc_ShouldReturnBookingsByStatus() {
        Booking booking = createBooking(item, booker,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                BookingStatus.WAITING);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("start").descending());
        List<Booking> result = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                booker.getId(), BookingStatus.WAITING, pageable);

        assertFalse(result.isEmpty());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return entityManager.persistAndFlush(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return entityManager.persistAndFlush(item);
    }

    private Booking createBooking(Item item, User booker, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return entityManager.persistAndFlush(booking);
    }
}