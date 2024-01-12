package ru.practicum.shareit.user;


import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private int lastId = 1;

    @Override
    public User createUser(User user) {
        validationEmail(user.getEmail());
        user.setId(getLastId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User updateUser(User user) {
        long id = user.getId();

        String oldEmail = users.get(id).getEmail();
        boolean checkNotOldEmail = user.getEmail() != oldEmail;

        if (checkNotOldEmail) {
            validationEmail(user.getEmail());
        }

        if (users.containsKey(id)) {
            users.put(id, user);
            if (checkNotOldEmail) {
                emails.remove(oldEmail);
            }
            return user;
        } else {
            throw new NotFoundException("Пользователь с id = " + id + " не найден.");
        }
    }

    @Override
    public void delUser(long userId) {
        if (users.containsKey(userId)) {
            emails.remove(users.get(userId).getEmail());
            users.remove(userId);
        } else {
            throw new NotFoundException("Не удалось удалить. Такой пользователь не найден.");
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUser(long userId) {
        User user = users.get(userId);
        if (user != null) {
            return user;
        } else {
            throw new NotFoundException("Такого пользователя нет");
        }
    }

    private void validationEmail(String email) {
        if (emails.contains(email)) {
            throw new ValidationException("Пользователь с таким email уже существует. Попробуйте другой.");
        }
    }

    private long getLastId() {
        return lastId++;
    }
}