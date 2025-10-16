package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private String name;

    @Email(message = "Email should be valid")
    private String email;
}