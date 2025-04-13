package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("Стартовал метод findAll");
        return userStorage.findAll();
    }

    public Set<User> getFriends(int id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new ResourceNotFoundException("Пользователь с id " + id + " не найден");
        }

        return user.getFriends().stream()
                .map(userStorage::getUser)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        return userStorage.update(newUser);
    }

    public User getUserById(Integer id) {
        return userStorage.getUser(id);
    }

    public User addFriend(Integer userId, Integer friendId) {
        User user = userStorage.findAll().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        User friend = userStorage.findAll().stream()
                .filter(u -> u.getId() == friendId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        if (user.getFriends().contains(friendId)) {
            throw new ConditionsNotMetException("Они уже являются друзьями");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);
        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        User user = userStorage.findAll().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        User friend = userStorage.findAll().stream()
                .filter(u -> u.getId() == friendId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", userId, friendId);

        return user;
    }

    public Set<User> getMutualFriends(int userId, int otherUserId) {
        User user = userStorage.findAll().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        User otherUser = userStorage.findAll().stream()
                .filter(u -> u.getId() == otherUserId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + otherUserId + " не найден"));

        Set<User> mutualFriends = new HashSet<>();
        for (Integer friendId : user.getFriends()) {
            if (otherUser.getFriends().contains(friendId)) {
                User friend = userStorage.findAll().stream()
                        .filter(u -> u.getId() == friendId)
                        .findFirst()
                        .orElseThrow(() -> new NotFoundException("Друг с id " + friendId + " не найден"));
                mutualFriends.add(friend);
            }
        }
        log.info("Общие друзья для пользователей с id {} и {}: {}", userId, otherUserId, mutualFriends);
        return mutualFriends;
    }
}
