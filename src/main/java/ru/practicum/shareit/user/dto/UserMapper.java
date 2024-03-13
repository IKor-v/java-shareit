package ru.practicum.shareit.user.dto;


import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;

@UtilityClass
public class UserMapper {
    public UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public User toUser(UserDto userDto, long userId) {
        User result = new User();
        result.setId(userId);
        result.setName(userDto.getName());
        result.setEmail(userDto.getEmail());
        return result;
    }

    public User toUser(UserDto userDto) {
        User result = new User();
        result.setName(userDto.getName());
        result.setEmail(userDto.getEmail());
        return result;
    }
}
