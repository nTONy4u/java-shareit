package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.util.List;

public interface BookingService {
    Booking createBooking(BookingCreateDto bookingDto, Long bookerId);

    Booking approveBooking(Long bookingId, Long ownerId, boolean approved);

    Booking cancelBooking(Long bookingId, Long userId);

    Booking getBookingById(Long bookingId, Long userId);

    List<Booking> getUserBookings(Long bookerId, String state, int from, int size);

    List<Booking> getOwnerBookings(Long ownerId, String state, int from, int size);
}