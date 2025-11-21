package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.CommentService;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class FullIntegrationCoverageTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ItemRequestService itemRequestService;

    @Test
    void testCompleteScenarioWithAllEdgeCases() {
        // Создаем пользователей
        User owner = createUser("Owner", "owner@example.com");
        User booker = createUser("Booker", "booker@example.com");
        User requester = createUser("Requester", "requester@example.com");

        // Создаем запрос
        ItemRequestCreateDto requestDto = new ItemRequestCreateDto("Need a drill for home project");
        var request = itemRequestService.createRequest(requestDto, requester.getId());
        assertNotNull(request);

        // Создаем предмет с запросом
        Item item = new Item();
        item.setName("Power Drill");
        item.setDescription("Heavy duty power drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(request);
        Item createdItem = itemService.createItem(item, owner.getId());

        // Тестируем бронирование
        testBookingScenarios(owner, booker, createdItem);

        // Тестируем поиск
        testSearchScenarios(owner);

        // Тестируем запросы
        testRequestScenarios(requester);
    }

    private void testBookingScenarios(User owner, User booker, Item item) {
        // Успешное бронирование
        BookingCreateDto validBooking = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        var booking = bookingService.createBooking(validBooking, booker.getId());
        assertNotNull(booking);

        // Одобрение бронирования
        var approvedBooking = bookingService.approveBooking(booking.getId(), owner.getId(), true);
        assertEquals("APPROVED", approvedBooking.getStatus().name());

        // Отклонение бронирования
        BookingCreateDto anotherBooking = new BookingCreateDto(
                item.getId(),
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(4)
        );
        var booking2 = bookingService.createBooking(anotherBooking, booker.getId());
        var rejectedBooking = bookingService.approveBooking(booking2.getId(), owner.getId(), false);
        assertEquals("REJECTED", rejectedBooking.getStatus().name());

        // Тестируем все состояния
        String[] states = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED", "CANCELED"};
        for (String state : states) {
            assertDoesNotThrow(() -> {
                List<?> userBookings = bookingService.getUserBookings(booker.getId(), state, 0, 10);
                List<?> ownerBookings = bookingService.getOwnerBookings(owner.getId(), state, 0, 10);
                assertNotNull(userBookings);
                assertNotNull(ownerBookings);
            });
        }
    }

    private void testSearchScenarios(User owner) {
        // Создаем несколько предметов для поиска
        Item hammer = createItem("Hammer", "Heavy duty hammer", true, owner);
        Item saw = createItem("Saw", "Electric saw", true, owner);
        Item unavailable = createItem("Unavailable Item", "Not available", false, owner);

        // Тестируем поиск
        List<Item> hammerResults = itemService.searchItems("hammer");
        assertFalse(hammerResults.isEmpty());

        List<Item> sawResults = itemService.searchItems("saw");
        assertFalse(sawResults.isEmpty());

        List<Item> unavailableResults = itemService.searchItems("unavailable");
        assertTrue(unavailableResults.isEmpty());

        // Граничные случаи поиска
        List<Item> emptySearch = itemService.searchItems("");
        assertTrue(emptySearch.isEmpty());

        List<Item> nullSearch = itemService.searchItems(null);
        assertTrue(nullSearch.isEmpty());

        List<Item> spaceSearch = itemService.searchItems("   ");
        assertTrue(spaceSearch.isEmpty());
    }

    private void testRequestScenarios(User requester) {
        // Создаем несколько запросов
        ItemRequestCreateDto request1 = new ItemRequestCreateDto("First request");
        ItemRequestCreateDto request2 = new ItemRequestCreateDto("Second request");
        ItemRequestCreateDto request3 = new ItemRequestCreateDto("Third request");

        var created1 = itemRequestService.createRequest(request1, requester.getId());
        var created2 = itemRequestService.createRequest(request2, requester.getId());
        var created3 = itemRequestService.createRequest(request3, requester.getId());

        // Получаем запросы пользователя
        var userRequests = itemRequestService.getUserRequests(requester.getId());
        assertTrue(userRequests.size() >= 3);

        // Получаем все запросы (для другого пользователя)
        User otherUser = createUser("Other User", "other@example.com");
        var allRequests = itemRequestService.getAllRequests(otherUser.getId(), 0, 10);
        assertFalse(allRequests.isEmpty());

        // Получаем конкретный запрос
        var foundRequest = itemRequestService.getRequestById(created1.getId(), requester.getId());
        assertEquals(created1.getId(), foundRequest.getId());
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userService.createUser(user);
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        return itemService.createItem(item, owner.getId());
    }
}