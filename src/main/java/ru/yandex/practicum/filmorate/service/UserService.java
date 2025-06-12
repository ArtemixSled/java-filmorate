package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
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

        return user.getFriends().keySet().stream()
                .map(userStorage::getUser)
                .filter(Objects::nonNull)
                .filter(friend -> friend.getFriends().get(id) == FriendshipStatus.CONFIRMED)
                .collect(Collectors.toSet());
    }

    public User createUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User updateUser(User newUser) {
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        return userStorage.update(newUser);
    }

    public User getUserById(Integer id) {
        User user = userStorage.getUser(id);
        if (user == null) throw new ResourceNotFoundException("Пользователь " + id + " не найден");
        return user;
    }

    public User addFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (friend.getFriends().get(userId) == FriendshipStatus.PENDING) {
            user.getFriends().put(friendId, FriendshipStatus.CONFIRMED);
            friend.getFriends().put(userId, FriendshipStatus.CONFIRMED);
            log.info("Пользователи {} и {} теперь друзья (confirmed)", userId, friendId);
        }

        else {
            if (user.getFriends().get(friendId) == FriendshipStatus.PENDING) {
                throw new ConditionsNotMetException("Запрос уже отправлен");
            }
            user.getFriends().put(friendId, FriendshipStatus.PENDING);
            log.info("Пользователь {} отправил запрос в друзья к {}", userId, friendId);
        }

        return user;
    }

    public User deleteFriend(int userId, int friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", userId, friendId);

        return user;
    }

    public Set<User> getMutualFriends(int userId, int otherUserId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherUserId);

        Set<User> friendsUser = getFriends(userId);
        Set<User> friendsOtherUser = getFriends(otherUserId);

        Set<User> mutualFriends = new HashSet<>();

        for (User friend : friendsUser) {
            if (friendsOtherUser.contains(friend)) {
                mutualFriends.add(friend);
            }
        }
        return mutualFriends;
    }
}
