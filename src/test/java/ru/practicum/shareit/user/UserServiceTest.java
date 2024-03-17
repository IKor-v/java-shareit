package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private final UserDto userDto = new UserDto(1L, "Link", "spuderman@man.com");
    private final User user = new User(1L, "Link", "spuderman@man.com");
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImp userService;

    @Test
    void addUserTest() {
        when(userRepository.save(user)).thenReturn(user);
        UserDto userResult = userService.createUser(userDto);

        assertEquals(userDto, userResult);
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserTest() {
        Long userId = user.getId();

        when(userRepository.saveAndFlush(user)).thenReturn(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto userResult = userService.updateUser(userDto, userId);

        assertEquals(userDto, userResult);
        verify(userRepository, times(1)).saveAndFlush(user);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserTest() {
        Long userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        UserDto userResult = userService.getUser(userId);

        assertEquals(userDto, userResult);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void delUserTest() {
        Long userId = user.getId();

        userService.delUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> result = userService.getAllUsers();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), userDto);
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserNotExistTest() {
        Long userId = user.getId() + 1;

        when(userRepository.findById(userId)).thenReturn(null);
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> userService.getUser(userId));

        assertEquals("Такой пользователь не найден", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void addUserWithExceptionTest() {
        when(userRepository.save(user)).thenReturn(null);
        ConflictException exception = Assertions.assertThrows(ConflictException.class, () -> userService.createUser(userDto));

        assertEquals("Не удалось сохранить пользователя, данные не верны", exception.getMessage());
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }


}
