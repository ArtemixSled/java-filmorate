package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.FriendRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.UserMapper.mapToUserDto;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Autowired
    public UserService(UserRepository userRepository, FriendRepository friendRepository) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
    }

    public List<UserDto> findAll() {
        log.info("Стартовал метод findAll");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getFriends(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));

        return friendRepository.findFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto createUser(NewUserRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            request.setName(request.getLogin());
        }
        User user = UserMapper.mapToUser(request);
        user = userRepository.save(user);
        return mapToUserDto(user);
    }

    public UserDto updateUser(Integer userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
        user.setLogin(request.getLogin());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setBirthday(request.getBirthday());

        User updated = userRepository.update(user);
        return UserMapper.mapToUserDto(updated);
    }

    public UserDto getUserById(Integer id) {
        return userRepository.findById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + id));
    }

    public UserDto addFriend(Integer userId, Integer friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + friendId));

        friendRepository.addFriend(userId, friendId);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}", userId, friendId);
        return mapToUserDto(user);
    }

    public UserDto deleteFriend(int userId, int friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + friendId));
        friendRepository.removeFriend(userId, friendId);

        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", userId, friendId);

        return mapToUserDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getMutualFriends(Integer userId, Integer otherUserId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId));
        userRepository.findById(otherUserId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + otherUserId));

        return friendRepository.findMutualFriends(userId, otherUserId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

}
