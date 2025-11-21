package ru.practicum.shareit.user.dto;

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
class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Autowired
    private ObjectMapper objectMapper;

    private final Validator validator;

    UserDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSerialize() throws Exception {
        UserDto dto = new UserDto(1L, "Test User", "test@email.com");

        var result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test User");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@email.com");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@email.com\"}";

        UserDto result = objectMapper.readValue(content, UserDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void whenNameIsBlank_thenValidationFails() {
        UserDto dto = new UserDto(null, "", "test@email.com");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Name cannot be blank");
    }

    @Test
    void whenEmailIsBlank_thenValidationFails() {
        UserDto dto = new UserDto(null, "Test User", "");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Email cannot be blank");
    }

    @Test
    void whenEmailIsInvalid_thenValidationFails() {
        UserDto dto = new UserDto(null, "Test User", "invalid-email");

        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Email should be valid");
    }
}