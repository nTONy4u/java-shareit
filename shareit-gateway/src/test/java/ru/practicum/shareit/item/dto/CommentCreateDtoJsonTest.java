package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentCreateDtoJsonTest {

    @Autowired
    private JacksonTester<CommentCreateDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSerialize() throws IOException {
        CommentCreateDto dto = new CommentCreateDto("Great item! Very useful.");

        JsonContent<CommentCreateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great item! Very useful.");
    }

    @Test
    void testDeserialize() throws IOException {
        String content = "{\"text\":\"Excellent condition, would recommend!\"}";

        CommentCreateDto dto = objectMapper.readValue(content, CommentCreateDto.class);

        assertThat(dto.getText()).isEqualTo("Excellent condition, would recommend!");
    }

    @Test
    void testDeserialize_EmptyText() throws IOException {
        String content = "{\"text\":\"\"}";

        CommentCreateDto dto = objectMapper.readValue(content, CommentCreateDto.class);

        assertThat(dto.getText()).isEmpty();
    }
}