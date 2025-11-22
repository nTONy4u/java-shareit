package ru.practicum.shareit.request.dto;

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
class ItemRequestCreateDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestCreateDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    ItemRequestCreateDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSerialize() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("Need a drill");

        var result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need a drill");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"description\":\"Need a drill\"}";

        ItemRequestCreateDto result = objectMapper.readValue(content, ItemRequestCreateDto.class);

        assertThat(result.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    void whenDescriptionIsBlank_thenValidationFails() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("");

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Description cannot be blank");
    }

    @Test
    void whenDescriptionIsNull_thenValidationFails() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto(null);

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Description cannot be blank");
    }
}