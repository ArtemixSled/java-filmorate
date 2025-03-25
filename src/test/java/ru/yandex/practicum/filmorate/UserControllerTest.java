package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void createUserValidData() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user123");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.create(user);

        assertNotNull(createdUser);
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getLogin(), createdUser.getLogin());
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getBirthday(), createdUser.getBirthday());
    }

    @Test
    void createUserWithInvalidEmail() {
        User user = new User();
        user.setEmail("invalidemail");
        user.setLogin("user123");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
        assertEquals("электронная почта не может быть пустой и должна содержать символ @", exception.getMessage());
    }

    @Test
    void createUserWithLoginContainingSpaces() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user 123");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
        assertEquals("логин не может быть пустым и содержать пробелы", exception.getMessage());
    }

    @Test
    void createUserWithFutureBirthday() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user123");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(2026, 1, 1));  // Future date

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
        assertEquals("дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void findAllUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1995, 5, 15));

        userController.create(user1);
        userController.create(user2);

        Collection<User> users = userController.findAll();

        assertEquals(2, users.size());
    }
}
