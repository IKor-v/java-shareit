package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody UserDto userDto) throws ValidationException {
        User newUser = userService.createUser(userDto);
        log.info("Добавлен пользователь с id = " + newUser.getId());
        return newUser;
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) throws ValidationException {
        User updateUser = userService.updateUser(userDto, userId);
        log.info("Данные пользователя с id = " + updateUser.getId() + " обновленны.");
        return updateUser;
    }

    @GetMapping("/{userId}")
    public User getUserForId(@PathVariable long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void delUser(@PathVariable long userId) {
        userService.delUser(userId);
        log.info("Пользователь с id = " + userId + " удалён.");
    }

}
