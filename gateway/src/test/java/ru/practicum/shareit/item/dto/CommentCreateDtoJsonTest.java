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
class CommentCreateDtoJsonTest {

    @Autowired
    private JacksonTester<CommentCreateDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    CommentCreateDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSerialize() throws Exception {
        CommentCreateDto dto = new CommentCreateDto("Great item!");

        var result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great item!");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"text\":\"Great item!\"}";

        CommentCreateDto result = objectMapper.readValue(content, CommentCreateDto.class);

        assertThat(result.getText()).isEqualTo("Great item!");
    }

    @Test
    void whenTextIsBlank_thenValidationFails() {
        CommentCreateDto dto = new CommentCreateDto("");

        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Comment text cannot be blank");
    }

    @Test
    void whenTextIsNull_thenValidationFails() {
        CommentCreateDto dto = new CommentCreateDto(null);

        Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Comment text cannot be blank");
    }
}