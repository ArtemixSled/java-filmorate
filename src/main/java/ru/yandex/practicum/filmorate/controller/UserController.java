package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {

    private Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Стартовал метод findAll");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody @Validated(User.Creation.class) User user) {
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

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Стартовал метод update с данными: {}", newUser);

        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
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

        if (users.containsKey(newUser.getId())) {
            log.info("Состояние пользователей до обновления: {}", users);
            users.put(newUser.getId(), newUser);
            log.info("Пользователь обновлен с id: {}", newUser.getId());
            log.info("Состояние пользователей после обновления: {}", users);

            return newUser;
        }

        String errorMessage = "Пользователь с id = " + newUser.getId() + " не найден";
        log.error(errorMessage);
        throw new NotFoundException(errorMessage);
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
