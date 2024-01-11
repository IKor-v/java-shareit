package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.ValidationException;
import java.util.Collection;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final long anyId = 0;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(UserDto userDto) {
        if (!validationUser(userDto)) {
            throw new ValidationException("Не удалось добавить пользователя: " + userDto.toString());
        }

        return userStorage.createUser(UserMapper.toUser(userDto, anyId));
    }

    public User updateUser(UserDto userDto, long userId) {
        if (userDto.getName() == "") {
            userDto.setName("Noname");
        }
        if (userDto.getEmail() == "") {
            userDto.setEmail("no@no");
        }
        if (!validationUser(userDto)) {
            throw new ValidationException("Не удалось обновить данные пользователя");
        }
        return userStorage.updateUser(UserMapper.toUser(userDto, userId));
    }

    public User getUser(long userId) {
        return userStorage.getUser(userId);
    }

    public void delUser(long userId) {
        userStorage.delUser(userId);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
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
