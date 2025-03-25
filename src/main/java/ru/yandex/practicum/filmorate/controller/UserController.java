package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.Instant;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private Map<Integer, User> users = new HashMap<>();

    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAll() {
        log.info("Стартовал метод findAll");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Получен запрос: {}", user);

        log.info("Текущая дата: {}", LocalDate.now());
        log.info("Дата рождения пользователя: {}", user.getBirthday());

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            String errorMessage = "электронная почта не может быть пустой и должна содержать символ @";
            log.error(errorMessage);
            throw new ConditionsNotMetException(errorMessage);
        } else if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            String errorMessage = "логин не может быть пустым и содержать пробелы";
            log.error(errorMessage);
            throw new ConditionsNotMetException(errorMessage);
        } else if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            String errorMessage = "дата рождения не может быть в будущем";
            log.error(errorMessage);
            throw new ConditionsNotMetException(errorMessage);
        }

        log.info("Дата рождения: {}", user.getBirthday());

        user.setId(getNextId());
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
        if (users.containsKey(newUser.getId())) {

            if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
                String errorMessage = "электронная почта не может быть пустой и должна содержать символ @";
                log.error(errorMessage);
                throw new ConditionsNotMetException(errorMessage);
            } else if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
                String errorMessage = "логин не может быть пустым и содержать пробелы";
                log.error(errorMessage);
                throw new ConditionsNotMetException(errorMessage);
            } else if (newUser.getName() == null || newUser.getLogin().isBlank()) {
                newUser.setName(newUser.getLogin());
            } else if (newUser.getBirthday().isAfter(LocalDate.now())) {
                String errorMessage = "дата рождения не может быть в будущем";
                log.error(errorMessage);
                throw new ConditionsNotMetException(errorMessage);
            }


            log.info("Состояние пользователей до создания: {}", users);
            users.put(newUser.getId(), newUser);
            log.info("Пользователь обновлен с id: {}", newUser.getId());
            log.info("Состояние пользователей после создания: {}", users);

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