package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestCreateDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestCreateDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws IOException {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Looking for a power drill for home renovation");

        JsonContent<ItemRequestCreateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Looking for a power drill for home renovation");
    }

    @Test
    void testDeserialize() throws IOException {
        String content = "{\"description\":\"Need a hammer for small repairs\"}";

        ItemRequestCreateDto dto = objectMapper.readValue(content, ItemRequestCreateDto.class);

        assertThat(dto.getDescription()).isEqualTo("Need a hammer for small repairs");
    }

    @Test
    void testDeserialize_LongDescription() throws IOException {
        String longDescription = "A".repeat(500);
        String content = "{\"description\":\"" + longDescription + "\"}";

        ItemRequestCreateDto dto = objectMapper.readValue(content, ItemRequestCreateDto.class);

        assertThat(dto.getDescription()).hasSize(500);
    }
}