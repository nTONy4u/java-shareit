package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoJsonTest {

    @Autowired
    private JacksonTester<BookingCreateDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    BookingCreateDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(1L, start, end);

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).hasJsonPathStringValue("$.end");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"itemId\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-01T12:00:00\"}";

        BookingCreateDto result = objectMapper.readValue(content, BookingCreateDto.class);

        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 1, 1, 10, 0));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 1, 1, 12, 0));
    }

    @Test
    void whenItemIdIsNull_thenValidationFails() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(null, start, end);

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Item ID cannot be null");
    }

    @Test
    void whenStartIsNull_thenValidationFails() {
        LocalDateTime end = LocalDateTime.now().plusHours(2);
        BookingCreateDto dto = new BookingCreateDto(1L, null, end);

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Start date cannot be null");
    }

    @Test
    void whenEndIsNull_thenValidationFails() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        BookingCreateDto dto = new BookingCreateDto(1L, start, null);

        Set<ConstraintViolation<BookingCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("End date cannot be null");
    }
}