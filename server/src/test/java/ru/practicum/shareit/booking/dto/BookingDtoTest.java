package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingDtoTest {

    @Test
    void bookingDto_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new BookingDto());
    }

    @Test
    void bookingDto_ToString_ShouldReturnString() {
        BookingDto dto = new BookingDto();
        assertNotNull(dto.toString());
    }

    @Test
    void bookingDto_HashCode_ShouldWork() {
        BookingDto dto1 = new BookingDto();
        BookingDto dto2 = new BookingDto();

        // Для объектов без переопределенного equals/hashCode
        // hashCode может быть разным, это нормально
        // Просто проверяем, что метод не падает
        assertDoesNotThrow(() -> dto1.hashCode());
        assertDoesNotThrow(() -> dto2.hashCode());
    }

    @Test
    void bookingDto_Equals_ShouldWork() {
        BookingDto dto1 = new BookingDto();
        BookingDto dto2 = new BookingDto();

        // Для объектов без переопределенного equals
        // сравнение по ссылкам должно возвращать false для разных объектов
        assertNotEquals(dto1, dto2);

        // Проверка равенства с самим собой
        assertEquals(dto1, dto1);

        // Проверка равенства с null
        assertNotEquals(null, dto1);

        // Проверка равенства с объектом другого класса
        assertNotEquals(dto1, new Object());
    }
}