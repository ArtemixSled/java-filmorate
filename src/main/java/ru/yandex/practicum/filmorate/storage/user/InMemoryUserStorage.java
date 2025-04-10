package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Integer, User> users = new HashMap<>();

    public Collection<User> findAll() {
        log.info("Стартовал метод findAll");
        return users.values();
    }

    public User getUser(Integer id) {
        return users.get(id);
    }

    public User create(User user) {
        log.info("Получен запрос на создание пользователя: {}", user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        log.info("Создание пользователя с id: {}", user.getId());

        log.info("Состояние пользователей до создания: {}", users);
        users.put(user.getId(), user);
        log.info("Пользователь создан с id: {}", user.getId());
        log.info("Состояние пользователей после создания: {}", users);

        return user;
    }

    public User update(User newUser) {
        log.info("Стартовал метод update с данными: {}", newUser);

        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!(users.containsKey(newUser.getId()))) {
            String errorMessage = "Пользователь с id = " + newUser.getId() + " не найден";
            log.error(errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }


        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }

        if (!newUser.isEmailValidForUpdate()) {
            throw new ConditionsNotMetException("Электронная почта должна быть в правильном формате");
        }

        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            newUser.setEmail(users.get(newUser.getId()).getEmail());
        }

        log.info("Состояние пользователей до обновления: {}", users);
        users.put(newUser.getId(), newUser);
        log.info("Пользователь обновлен с id: {}", newUser.getId());
        log.info("Состояние пользователей после обновления: {}", users);

        return newUser;
    }

    private int getNextId() {
        log.info("Стартовал метод getNextId");
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        log.info("Возвращается следующий id: {}", currentMaxId + 1);
        return ++currentMaxId;
    }
}
