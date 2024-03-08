package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceForMemoryRepository implements UserService {
    private final UserRepository userRepository;
    private final long anyId = 0;

    @Autowired
    public UserServiceForMemoryRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (!validationUser(userDto)) {
            throw new ValidationException("Не удалось добавить пользователя: " + userDto.toString());
        }

        return UserMapper.toUserDto(userRepository.createUser(UserMapper.toUser(userDto, anyId)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User oldUser = userRepository.getUser(userId);
        if (userDto.getName() == null) {
            userDto.setName(oldUser.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(oldUser.getEmail());
        }
        if (!validationUser(userDto)) {
            throw new ValidationException("Не удалось обновить данные пользователя");
        }
        return UserMapper.toUserDto(userRepository.updateUser(UserMapper.toUser(userDto, userId)));
    }

    @Override
    public UserDto getUser(long userId) {
        return UserMapper.toUserDto(userRepository.getUser(userId));
    }

    @Override
    public void delUser(long userId) {
        userRepository.delUser(userId);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }


    private boolean validationUser(UserDto userDto) throws ValidationException {
        String message = "Ошибка валидации пользователя: ";
        if (userDto == null) {
            message += "переданно пустое тело.";
            throw new ValidationException(message);
        }
        if ((userDto.getEmail().isBlank()) || !(userDto.getEmail().contains("@"))) {
            message += "адрес электронной почты не может быть пустым или без '@'.";
        } else if (userDto.getName().isBlank()) {
            message += "имя не может быть пустым";
        } else {
            return true;
        }
        throw new ValidationException(message);
    }
}
