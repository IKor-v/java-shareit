package ru.practicum.shareit.user.dto;


import ru.practicum.shareit.user.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto, long userId) {
        User result = new User();
        result.setId(userId);
        result.setName(userDto.getName());
        result.setEmail(userDto.getEmail());
        return result;
    }

    public static User toUser(UserDto userDto) {
        User result = new User();
        result.setName(userDto.getName());
        result.setEmail(userDto.getEmail());
        return result;
    }
}
