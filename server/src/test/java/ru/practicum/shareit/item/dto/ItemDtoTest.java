package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoTest {

    @Test
    void itemDto_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new ItemDto());
    }

    @Test
    void itemDto_AllArgsConstructor_ShouldSetFields() {
        ItemDto itemDto = new ItemDto(1L, "Item", "Description", true, null, null, null, null);

        assertEquals(1L, itemDto.getId());
        assertEquals("Item", itemDto.getName());
        assertEquals("Description", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
    }

    @Test
    void itemDto_EqualsAndHashCode_ShouldWorkCorrectly() {
        ItemDto dto1 = new ItemDto(1L, "Item", "Desc", true, null, null, null, null);
        ItemDto dto2 = new ItemDto(1L, "Item", "Desc", true, null, null, null, null);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void itemDto_ToString_ShouldReturnString() {
        ItemDto dto = new ItemDto();
        assertNotNull(dto.toString());
    }

    @Test
    void bookingInfo_DefaultConstructor_ShouldCreateObject() {
        assertDoesNotThrow(() -> new ItemDto.BookingInfo());
    }

    @Test
    void bookingInfo_AllArgsConstructor_ShouldSetFields() {
        ItemDto.BookingInfo bookingInfo = new ItemDto.BookingInfo(1L, 2L);

        assertEquals(1L, bookingInfo.getId());
        assertEquals(2L, bookingInfo.getBookerId());
    }

    @Test
    void bookingInfo_EqualsAndHashCode_ShouldWorkCorrectly() {
        ItemDto.BookingInfo info1 = new ItemDto.BookingInfo(1L, 2L);
        ItemDto.BookingInfo info2 = new ItemDto.BookingInfo(1L, 2L);

        assertEquals(info1, info2);
        assertEquals(info1.hashCode(), info2.hashCode());
    }

    @Test
    void bookingInfo_ToString_ShouldReturnString() {
        ItemDto.BookingInfo info = new ItemDto.BookingInfo();
        assertNotNull(info.toString());
    }
}