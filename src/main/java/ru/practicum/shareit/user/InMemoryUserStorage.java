package ru.practicum.shareit.user;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final List<String> emails = new ArrayList<>();
    private int lastId = 1;

    public User createUser(User user) {
        if (emails.contains(user.getEmail())) {
            throw new ValidationException("Пользователь с таким email уже существует. Попробуйте другой.");
        }
        user.setId(getLastId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    public User updateUser(User user) {
        long id = user.getId();

        if (user.getName() == "Noname") {
            user.setName(users.get(id).getName());
        }
        if (user.getEmail() == "no@no") {
            user.setEmail(users.get(id).getEmail());
        }

        String oldEmail = users.get(id).getEmail();
        boolean checkNotOldEmail = user.getEmail() != oldEmail;

        if ((checkNotOldEmail) && (emails.contains(user.getEmail()))) {
            throw new ValidationException("Пользователь с таким email уже существует. Попробуйте другой.");
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

    private long getLastId() {
        return lastId++;
    }


    public User getUser(long userId) {
        User user = users.get(userId);
        if (user != null) {
            return user;
        } else {
            throw new NotFoundException("Такого пользователя нет");
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

}