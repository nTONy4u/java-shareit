package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemBookingInfoServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private ItemBookingInfoService itemBookingInfoService;

    @Test
    void getLastBookingForItem_WithBooking_ShouldReturnBooking() {
        User owner = new User(1L, "Owner", "owner@example.com");
        User booker = new User(2L, "Booker", "booker@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), item, booker, BookingStatus.APPROVED);

        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(
                eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.of(booking));

        Booking result = itemBookingInfoService.getLastBookingForItem(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getLastBookingForItem_NoBooking_ShouldReturnNull() {
        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(
                eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.empty());

        Booking result = itemBookingInfoService.getLastBookingForItem(1L);

        assertNull(result);
    }

    @Test
    void getNextBookingForItem_WithBooking_ShouldReturnBooking() {
        User owner = new User(1L, "Owner", "owner@example.com");
        User booker = new User(2L, "Booker", "booker@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);

        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.of(booking));

        Booking result = itemBookingInfoService.getNextBookingForItem(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getNextBookingForItem_NoBooking_ShouldReturnNull() {
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Optional.empty());

        Booking result = itemBookingInfoService.getNextBookingForItem(1L);

        assertNull(result);
    }

    @Test
    void hasUserBookedItem_WithBookings_ShouldReturnTrue() {
        when(bookingRepository.findCompletedBookingsByBookerAndItem(
                eq(1L), eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(List.of(new Booking()));

        boolean result = itemBookingInfoService.hasUserBookedItem(1L, 1L);

        assertTrue(result);
    }

    @Test
    void hasUserBookedItem_NoBookings_ShouldReturnFalse() {
        when(bookingRepository.findCompletedBookingsByBookerAndItem(
                eq(1L), eq(1L), any(LocalDateTime.class), eq(BookingStatus.APPROVED)))
                .thenReturn(Collections.emptyList());

        boolean result = itemBookingInfoService.hasUserBookedItem(1L, 1L);

        assertFalse(result);
    }
}