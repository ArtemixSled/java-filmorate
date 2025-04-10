package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.Set;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.service.UserService;

@RestController
@RequestMapping("/users")
@Validated
@Slf4j
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@RequestBody @Validated(User.Creation.class) User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        User user = userService.getUserById(newUser.getId());
        if (user == null) {
            throw new ResourceNotFoundException("Не найден пользователь с id: " + newUser.getId());
        }
        return userService.updateUser(newUser);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException("Не найден пользователь с id: " + id);
        }
        return user;
    }


    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        User user = userService.getUserById(userId);
        User friend = userService.getUserById(friendId);
        if (user == null) {
            throw new ResourceNotFoundException("Не найден пользователь с id: " + userId);
        }
        if (friend == null) {
            throw new ResourceNotFoundException("Не найден пользователь с id: " + friendId);
        }
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriend(@PathVariable int userId, @PathVariable int friendId) {
        User user = userService.getUserById(userId);
        User friend = userService.getUserById(friendId);
        if (user == null) {
            throw new ResourceNotFoundException("Не найден пользователь с id: " + userId);
        }
        if (friend == null) {
            throw new ResourceNotFoundException("Не найден пользователь с id: " + friendId);
        }
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable int id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException("Не найден пользователь с id: " + id);
        }
        return userService.getFriends(id);
    }


    @GetMapping("/{userId}/friends/common/{otherId}")
    public Set<User> getMutualFriends(@PathVariable int userId, @PathVariable int otherId) {
        User user = userService.getUserById(userId);
        User other = userService.getUserById(otherId);
        if (user == null) {
            throw new ValidationException("Не найден пользователь с id: " + userId);
        }
        if (other == null) {
            throw new ValidationException("Не найден пользователь с id: " + otherId);
        }
        return userService.getMutualFriends(userId, otherId);
    }
}