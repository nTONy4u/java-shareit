package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryEdgeCasesTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findCurrentBookingsByBooker_WithNoBookings_ShouldReturnEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> result = bookingRepository.findCurrentBookingsByBooker(
                999L, LocalDateTime.now(), pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void findCurrentBookingsByOwner_WithNoBookings_ShouldReturnEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Booking> result = bookingRepository.findCurrentBookingsByOwner(
                999L, LocalDateTime.now(), pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc_WithNoResults_ShouldReturnEmpty() {
        Optional<Booking> result = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(
                999L, LocalDateTime.now(), BookingStatus.APPROVED);

        assertTrue(result.isEmpty());
    }

    @Test
    void findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc_WithNoResults_ShouldReturnEmpty() {
        Optional<Booking> result = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                999L, LocalDateTime.now(), BookingStatus.APPROVED);

        assertTrue(result.isEmpty());
    }

    @Test
    void findCompletedBookingsByBookerAndItem_WithNoBookings_ShouldReturnEmpty() {
        List<Booking> result = bookingRepository.findCompletedBookingsByBookerAndItem(
                999L, 999L, LocalDateTime.now(), BookingStatus.APPROVED);

        assertTrue(result.isEmpty());
    }

    @Test
    void findWaitingBookingByIdAndBooker_WithNoBooking_ShouldReturnEmpty() {
        Optional<Booking> result = bookingRepository.findWaitingBookingByIdAndBooker(999L, 999L);
        assertTrue(result.isEmpty());
    }
}