package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplFullCoverageTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBooking_WithSameStartAndEnd_ShouldThrowException() {
        LocalDateTime sameTime = LocalDateTime.now().plusDays(1);
        BookingCreateDto bookingDto = new BookingCreateDto(1L, sameTime, sameTime);
        User booker = new User(2L, "Booker", "booker@example.com");
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);

        when(userService.getUserById(2L)).thenReturn(booker);
        when(itemService.getItemById(1L)).thenReturn(item);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto, 2L));

        assertEquals("Invalid booking dates", exception.getMessage());
    }

    @Test
    void createBooking_WithEndBeforeStart_ShouldThrowException() {
        BookingCreateDto bookingDto = new BookingCreateDto(1L,
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(1));
        User booker = new User(2L, "Booker", "booker@example.com");
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);

        when(userService.getUserById(2L)).thenReturn(booker);
        when(itemService.getItemById(1L)).thenReturn(item);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto, 2L));

        assertEquals("Invalid booking dates", exception.getMessage());
    }

    @Test
    void createBooking_WithStartInPast_ShouldThrowException() {
        BookingCreateDto bookingDto = new BookingCreateDto(1L,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        User booker = new User(2L, "Booker", "booker@example.com");
        User owner = new User(1L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);

        when(userService.getUserById(2L)).thenReturn(booker);
        when(itemService.getItemById(1L)).thenReturn(item);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.createBooking(bookingDto, 2L));

        assertEquals("Start date cannot be in the past", exception.getMessage());
    }

    @Test
    void approveBooking_AlreadyApproved_ShouldThrowException() {
        User owner = new User(1L, "Owner", "owner@example.com");
        User booker = new User(2L, "Booker", "booker@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.APPROVED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(1L, 1L, true));

        assertEquals("Booking already processed", exception.getMessage());
    }

    @Test
    void approveBooking_AlreadyRejected_ShouldThrowException() {
        User owner = new User(1L, "Owner", "owner@example.com");
        User booker = new User(2L, "Booker", "booker@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.REJECTED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(1L, 1L, true));

        assertEquals("Booking already processed", exception.getMessage());
    }

    @Test
    void getBookingById_UserIsNeitherBookerNorOwner_ShouldThrowException() {
        User owner = new User(1L, "Owner", "owner@example.com");
        User booker = new User(2L, "Booker", "booker@example.com");
        User otherUser = new User(3L, "Other", "other@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(1L, 3L));

        assertEquals("Booking not found for user: 3", exception.getMessage());
    }

    @Test
    void getUserBookings_WithCanceledState_ShouldReturnCanceledBookings() {
        User booker = new User(1L, "Booker", "booker@example.com");
        when(userService.getUserById(1L)).thenReturn(booker);

        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                eq(1L), eq(BookingStatus.CANCELED), any()))
                .thenReturn(List.of(new Booking()));

        List<Booking> result = bookingService.getUserBookings(1L, "CANCELED", 0, 10);

        assertFalse(result.isEmpty());
    }

    @Test
    void getOwnerBookings_WithCanceledState_ShouldReturnCanceledBookings() {
        User owner = new User(1L, "Owner", "owner@example.com");
        when(userService.getUserById(1L)).thenReturn(owner);

        when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                eq(1L), eq(BookingStatus.CANCELED), any()))
                .thenReturn(List.of(new Booking()));

        List<Booking> result = bookingService.getOwnerBookings(1L, "CANCELED", 0, 10);

        assertFalse(result.isEmpty());
    }

    @Test
    void getUserBookings_WithUnknownState_ShouldThrowException() {
        User booker = new User(1L, "Booker", "booker@example.com");
        when(userService.getUserById(1L)).thenReturn(booker);

        assertThrows(ValidationException.class,
                () -> bookingService.getUserBookings(1L, "UNKNOWN_STATE", 0, 10));
    }

    @Test
    void getOwnerBookings_WithUnknownState_ShouldThrowException() {
        User owner = new User(1L, "Owner", "owner@example.com");
        when(userService.getUserById(1L)).thenReturn(owner);

        assertThrows(ValidationException.class,
                () -> bookingService.getOwnerBookings(1L, "INVALID_STATE", 0, 10));
    }

    @Test
    void createBooking_ItemNotFound_ShouldThrowException() {
        BookingCreateDto bookingDto = new BookingCreateDto(1L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        User booker = new User(2L, "Booker", "booker@example.com");

        when(userService.getUserById(2L)).thenReturn(booker);
        when(itemService.getItemById(1L)).thenThrow(new ItemNotFoundException("Item not found"));

        assertThrows(ItemNotFoundException.class,
                () -> bookingService.createBooking(bookingDto, 2L));
    }

    @Test
    void approveBooking_BookingNotFound_ShouldThrowException() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.approveBooking(999L, 1L, true));
    }

    @Test
    void getBookingById_BookingNotFound_ShouldThrowException() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(999L, 1L));
    }

    @Test
    void cancelBooking_ValidCancellation_ShouldCancelBooking() {
        User booker = new User(1L, "Booker", "booker@example.com");
        User owner = new User(2L, "Owner", "owner@example.com");
        Item item = new Item(1L, "Item", "Description", true, owner, null);
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, booker, BookingStatus.WAITING);

        when(bookingRepository.findWaitingBookingByIdAndBooker(1L, 1L))
                .thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.cancelBooking(1L, 1L);

        assertEquals(BookingStatus.CANCELED, result.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancelBooking_NonWaitingBooking_ShouldThrowException() {
        when(bookingRepository.findWaitingBookingByIdAndBooker(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.cancelBooking(1L, 1L));
    }
}