package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
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
    @Transactional
    public UserDto updateUser(UserDto userDto, long userId) {
        User oldUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Нельзя обновить пользователя, которого не существует."));
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
            return UserMapper.toUserDto(userRepository.saveAndFlush(UserMapper.toUser(userDto, userId)));
        } catch (Exception e) {
            throw new ConflictException("Не удалось обновить данные пользователя, данные не верны");
        }
    }

    @Override
    public UserDto getUser(long userId) {
        try {
            return UserMapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Не удалось найти такого пользователя.")));
        } catch (Exception e) {
            throw new NotFoundException("Такой пользователь не найден");
        }
    }

    @Override
    @Transactional
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
