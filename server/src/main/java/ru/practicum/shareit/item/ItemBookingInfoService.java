package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ItemBookingInfoService {
    private final BookingRepository bookingRepository;

    public Booking getLastBookingForItem(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByStartDesc(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED).orElse(null);
    }

    public Booking getNextBookingForItem(Long itemId) {
        return bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED).orElse(null);
    }

    public boolean hasUserBookedItem(Long userId, Long itemId) {
        return !bookingRepository.findCompletedBookingsByBookerAndItem(
                userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED).isEmpty();
    }
}