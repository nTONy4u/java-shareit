package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static User toUser(UserUpdateDto userUpdateDto) {
        User user = new User();
        user.setName(userUpdateDto.getName());
        user.setEmail(userUpdateDto.getEmail());
        return user;
    }
}