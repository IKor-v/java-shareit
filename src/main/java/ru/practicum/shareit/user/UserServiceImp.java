package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.stream.Collectors;

@Primary
@Service
public class UserServiceImp implements UserService {
    private final DatabaseUserRepository userRepository;

    @Autowired
    public UserServiceImp(DatabaseUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (!validationUser(userDto)) {
            throw new ValidationException("Не удалось добавить пользователя: " + userDto.toString());
        }
        try {
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
        } catch (Exception e) {
            throw new ConflictException("Не удалось сохранить пользователя, данные не верны");
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User oldUser = userRepository.findById(userId).get();
        if (userDto.getName() == null) {
            userDto.setName(oldUser.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(oldUser.getEmail());
        }
        if (!validationUser(userDto)) {
            throw new ValidationException("Не удалось обновить данные пользователя");
        }
        try {
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto, userId)));
        } catch (Exception e) {
            throw new ConflictException("Не удалось обновить данные пользователя, данные не верны");
        }
    }

    @Override
    public UserDto getUser(long userId) {
        try {
            return UserMapper.toUserDto(userRepository.findById(userId).get());
        } catch (Exception e) {
            throw new NotFoundException("Такой пользователь не найден");
        }
    }

    @Override
    public void delUser(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());  //поставить ограничения
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