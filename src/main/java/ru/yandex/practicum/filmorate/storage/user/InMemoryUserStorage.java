package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User getUser(Integer id) {
        return users.get(id);
    }

    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    public User update(User newUser) {
        if (!(users.containsKey(newUser.getId()))) {
            String errorMessage = "Пользователь с id = " + newUser.getId() + " не найден";
            throw new ResourceNotFoundException(errorMessage);
        }
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
