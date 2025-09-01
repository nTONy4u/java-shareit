package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws IOException {
        ItemDto.BookingInfo lastBooking = new ItemDto.BookingInfo(1L, 10L);
        ItemDto.BookingInfo nextBooking = new ItemDto.BookingInfo(2L, 20L);
        CommentDto comment = new CommentDto(1L, "Great item!", "John Doe", null);

        ItemDto itemDto = new ItemDto(
                1L,
                "Drill",
                "Powerful electric drill",
                true,
                100L,
                List.of(comment),
                lastBooking,
                nextBooking
        );

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Drill");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Powerful electric drill");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(100);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(10);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(20);
        assertThat(result).extractingJsonPathArrayValue("$.comments").hasSize(1);
    }

    @Test
    void testDeserialize() throws IOException {
        String content = "{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful drill\",\"available\":true,\"requestId\":100}";

        ItemDto itemDto = objectMapper.readValue(content, ItemDto.class);

        assertThat(itemDto.getId()).isEqualTo(1L);
        assertThat(itemDto.getName()).isEqualTo("Drill");
        assertThat(itemDto.getDescription()).isEqualTo("Powerful drill");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isEqualTo(100L);
    }
}