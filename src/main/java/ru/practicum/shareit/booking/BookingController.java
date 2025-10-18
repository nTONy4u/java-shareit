package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
            @Valid @RequestBody BookingCreateDto bookingDto,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating booking for user {}: {}", userId, bookingDto);
        Booking booking = bookingService.createBooking(bookingDto, userId);
        return ResponseEntity.ok(BookingMapper.toResponseDto(booking));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approveBooking(
            @PathVariable Long bookingId,
            @RequestParam boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Approving booking {} by user {}: {}", bookingId, userId, approved);
        Booking booking = bookingService.approveBooking(bookingId, userId, approved);
        return ResponseEntity.ok(BookingMapper.toResponseDto(booking));
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponseDto> cancelBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Canceling booking {} by user {}", bookingId, userId);
        Booking booking = bookingService.cancelBooking(bookingId, userId);
        return ResponseEntity.ok(BookingMapper.toResponseDto(booking));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting booking {} for user {}", bookingId, userId);
        Booking booking = bookingService.getBookingById(bookingId, userId);
        return ResponseEntity.ok(BookingMapper.toResponseDto(booking));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Getting bookings for user {} with state {}", userId, state);
        List<Booking> bookings = bookingService.getUserBookings(userId, state, from, size);
        return ResponseEntity.ok(bookings.stream()
                .map(BookingMapper::toResponseDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Getting owner bookings for user {} with state {}", userId, state);
        List<Booking> bookings = bookingService.getOwnerBookings(userId, state, from, size);
        return ResponseEntity.ok(bookings.stream()
                .map(BookingMapper::toResponseDto)
                .collect(Collectors.toList()));
    }
}