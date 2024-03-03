package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserRepository {
    User createUser(User user);

    User updateUser(User user);

    User getUser(long userId);

    void delUser(long userId);

    Collection<User> getAllUsers();
}
