package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemAccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookingService(BookingRepository bookingRepository, UserService userService, ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Transactional
    public Booking createBooking(BookingCreateDto bookingDto, Long bookerId) {
        User booker = userService.getUserById(bookerId);
        Item item = itemService.getItemById(bookingDto.getItemId());

        validateBooking(bookingDto, bookerId, item);

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created with id: {} for user: {} and item: {}",
                savedBooking.getId(), bookerId, item.getId());
        return savedBooking;
    }

    private void validateBooking(BookingCreateDto bookingDto, Long bookerId, Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new ItemNotFoundException("Owner cannot book their own item");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException("Invalid booking dates");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date cannot be in the past");
        }
    }

    @Transactional
    public Booking approveBooking(Long bookingId, Long ownerId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ItemAccessDeniedException("Only item owner can approve booking");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking already processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking {} {} by owner {}", bookingId, approved ? "approved" : "rejected", ownerId);
        return updatedBooking;
    }

    @Transactional
    public Booking cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findWaitingBookingByIdAndBooker(bookingId, userId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found or cannot be canceled. Only waiting bookings can be canceled by booker."));

        booking.setStatus(BookingStatus.CANCELED);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking {} canceled by user {}", bookingId, userId);
        return updatedBooking;
    }

    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new BookingNotFoundException("Booking not found for user: " + userId);
        }

        return booking;
    }

    public List<Booking> getUserBookings(Long bookerId, String state, int from, int size) {
        userService.getUserById(bookerId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                return bookingRepository.findByBookerIdOrderByStartDesc(bookerId, pageable);
            case "CURRENT":
                return bookingRepository.findCurrentBookingsByBooker(bookerId, now, pageable);
            case "PAST":
                return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now, pageable);
            case "FUTURE":
                return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now, pageable);
            case "WAITING":
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING, pageable);
            case "REJECTED":
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED, pageable);
            case "CANCELED":
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, BookingStatus.CANCELED, pageable);
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    public List<Booking> getOwnerBookings(Long ownerId, String state, int from, int size) {
        userService.getUserById(ownerId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        LocalDateTime now = LocalDateTime.now();

        switch (state.toUpperCase()) {
            case "ALL":
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable);
            case "CURRENT":
                return bookingRepository.findCurrentBookingsByOwner(ownerId, now, pageable);
            case "PAST":
                return bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now, pageable);
            case "FUTURE":
                return bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now, pageable);
            case "WAITING":
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, pageable);
            case "REJECTED":
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED, pageable);
            case "CANCELED":
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.CANCELED, pageable);
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }
}