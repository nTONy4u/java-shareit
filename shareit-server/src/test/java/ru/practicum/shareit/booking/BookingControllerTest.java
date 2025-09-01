package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private Booking createCompleteBooking(Long id, User booker, Item item) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    private User createUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Item createItem(Long id, String name, User owner) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(owner);
        return item;
    }

    @Test
    void createBooking_ValidBooking_ShouldReturnCreatedBooking() throws Exception {
        BookingCreateDto bookingDto = new BookingCreateDto(1L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        User booker = createUser(1L, "Booker", "booker@example.com");
        User owner = createUser(2L, "Owner", "owner@example.com");
        Item item = createItem(1L, "Test Item", owner);
        Booking booking = createCompleteBooking(1L, booker, item);

        when(bookingService.createBooking(
                ArgumentMatchers.any(BookingCreateDto.class),
                ArgumentMatchers.eq(1L))
        ).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk());
    }

    @Test
    void approveBooking_ValidApproval_ShouldReturnUpdatedBooking() throws Exception {
        User booker = createUser(1L, "Booker", "booker@example.com");
        User owner = createUser(2L, "Owner", "owner@example.com");
        Item item = createItem(1L, "Test Item", owner);
        Booking booking = createCompleteBooking(1L, booker, item);
        booking.setStatus(BookingStatus.APPROVED);

        when(bookingService.approveBooking(
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.eq(2L),
                ArgumentMatchers.eq(true))
        ).thenReturn(booking);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking_ExistingBooking_ShouldReturnBooking() throws Exception {
        User booker = createUser(1L, "Booker", "booker@example.com");
        User owner = createUser(2L, "Owner", "owner@example.com");
        Item item = createItem(1L, "Test Item", owner);
        Booking booking = createCompleteBooking(1L, booker, item);

        when(bookingService.getBookingById(
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.eq(1L))
        ).thenReturn(booking);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking_NonExistingBooking_ShouldReturnNotFound() throws Exception {
        when(bookingService.getBookingById(
                ArgumentMatchers.eq(999L),
                ArgumentMatchers.eq(1L))
        ).thenThrow(new BookingNotFoundException("Booking not found"));

        mockMvc.perform(get("/bookings/999")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserBookings_WithState_ShouldReturnBookings() throws Exception {
        User booker = createUser(1L, "Booker", "booker@example.com");
        User owner = createUser(2L, "Owner", "owner@example.com");
        Item item = createItem(1L, "Test Item", owner);

        Booking booking1 = createCompleteBooking(1L, booker, item);
        Booking booking2 = createCompleteBooking(2L, booker, item);
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingService.getUserBookings(
                ArgumentMatchers.eq(1L),
                ArgumentMatchers.eq("ALL"),
                ArgumentMatchers.eq(0),
                ArgumentMatchers.eq(10))
        ).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getOwnerBookings_WithState_ShouldReturnBookings() throws Exception {
        User booker = createUser(1L, "Booker", "booker@example.com");
        User owner = createUser(2L, "Owner", "owner@example.com");
        Item item = createItem(1L, "Test Item", owner);

        Booking booking1 = createCompleteBooking(1L, booker, item);
        List<Booking> bookings = Arrays.asList(booking1);

        when(bookingService.getOwnerBookings(
                ArgumentMatchers.eq(2L),
                ArgumentMatchers.eq("CURRENT"),
                ArgumentMatchers.eq(0),
                ArgumentMatchers.eq(10))
        ).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2L)
                        .param("state", "CURRENT")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}