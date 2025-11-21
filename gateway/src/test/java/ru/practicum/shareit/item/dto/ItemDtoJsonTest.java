package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    ItemDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSerialize() throws Exception {
        ItemDto dto = new ItemDto(1L, "Test Item", "Test Description", true, null, null, null, null);

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test Item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true}";

        ItemDto result = objectMapper.readValue(content, ItemDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Item");
        assertThat(result.getDescription()).isEqualTo("Test Description");
        assertThat(result.getAvailable()).isEqualTo(true);
    }

    @Test
    void whenNameIsBlank_thenValidationFails() {
        ItemDto dto = new ItemDto(null, "", "Test Description", true, null, null, null, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Name cannot be blank");
    }

    @Test
    void whenDescriptionIsBlank_thenValidationFails() {
        ItemDto dto = new ItemDto(null, "Test Item", "", true, null, null, null, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Description cannot be blank");
    }

    @Test
    void whenAvailableIsNull_thenValidationFails() {
        ItemDto dto = new ItemDto(null, "Test Item", "Test Description", null, null, null, null, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Available status cannot be null");
    }
}