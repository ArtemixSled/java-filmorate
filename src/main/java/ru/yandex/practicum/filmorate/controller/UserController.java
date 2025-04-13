package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
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
    public User update(@RequestBody @Validated(User.Update.class) User newUser) {
        getRequiredUser(newUser.getId());
        return userService.updateUser(newUser);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        return getRequiredUser(id);
    }


    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable Integer userId, @PathVariable Integer friendId) {
        getRequiredUser(userId);
        getRequiredUser(friendId);
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriend(@PathVariable int userId, @PathVariable int friendId) {
        getRequiredUser(userId);
        getRequiredUser(friendId);
        return userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable int id) {
        getRequiredUser(id);
        return userService.getFriends(id);
    }


    @GetMapping("/{userId}/friends/common/{otherId}")
    public Set<User> getMutualFriends(@PathVariable int userId, @PathVariable int otherId) {
        getRequiredUser(userId);
        getRequiredUser(otherId);
        return userService.getMutualFriends(userId, otherId);
    }

    private User getRequiredUser(int userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("Не найден пользователь с id: " + userId);
        }
        return user;
    }
}