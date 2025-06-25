package ru.yandex.practicum.filmorate.integrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void testCreateUser_DefaultNameFromLogin() {
        NewUserRequest req = NewUserRequest.builder()
                .login("noNameUser")
                .email("no.name@test.ru")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        UserDto dto = userService.createUser(req);

        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getName()).isEqualTo("noNameUser");
    }

    @Test
    void testCreateUser_WithCustomName() {
        NewUserRequest req = NewUserRequest.builder()
                .login("userX")
                .email("userx@test.ru")
                .name("Custom Name")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();

        UserDto dto = userService.createUser(req);

        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getName()).isEqualTo("Custom Name");
    }

    @Test
    void testGetUserById_Success() {
        NewUserRequest req = NewUserRequest.builder()
                .login("findMe")
                .email("find@test.ru")
                .name("Finder")
                .birthday(LocalDate.of(1992, 2, 2))
                .build();
        UserDto created = userService.createUser(req);

        UserDto found = userService.getUserById(created.getId());
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
    }

    @Test
    void testGetUserById_NotFound() {
        assertThatThrownBy(() -> userService.getUserById(9999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }

    @Test
    void testFindAllUsers() {
        userService.createUser(NewUserRequest.builder()
                .login("a1").email("a1@test.ru").name("A1").birthday(LocalDate.of(1990,1,1)).build());
        userService.createUser(NewUserRequest.builder()
                .login("a2").email("a2@test.ru").name("A2").birthday(LocalDate.of(1991,1,1)).build());

        List<UserDto> all = userService.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateUser_Success() {
        NewUserRequest createReq = NewUserRequest.builder()
                .login("updUser").email("upd@test.ru").name("Before").birthday(LocalDate.of(1990,1,1)).build();
        UserDto created = userService.createUser(createReq);

        UpdateUserRequest updateReq = UpdateUserRequest.builder()
                .login("updUser").email("after@test.ru").name("After").birthday(LocalDate.of(1990,1,1)).build();
        UserDto updated = userService.updateUser(created.getId(), updateReq);

        assertThat(updated.getName()).isEqualTo("After");
        assertThat(updated.getEmail()).isEqualTo("after@test.ru");
    }

    @Test
    void testAddAndRemoveFriend_ViaService() {
        UserDto u1 = userService.createUser(NewUserRequest.builder()
                .login("u1").email("u1@test.ru").name("U1").birthday(LocalDate.of(2000,1,1)).build());
        UserDto u2 = userService.createUser(NewUserRequest.builder()
                .login("u2").email("u2@test.ru").name("U2").birthday(LocalDate.of(2001,1,1)).build());

        userService.addFriend(u1.getId(), u2.getId());
        List<UserDto> friends = userService.getFriends(u1.getId());
        assertThat(friends).extracting(UserDto::getId).contains(u2.getId());

        userService.deleteFriend(u1.getId(), u2.getId());
        assertThat(userService.getFriends(u1.getId())).isEmpty();
    }

    @Test
    void testGetMutualFriends() {
        UserDto u1 = userService.createUser(NewUserRequest.builder()
                .login("m1").email("m1@test.ru").name("M1").birthday(LocalDate.of(1990,1,1)).build());
        UserDto u2 = userService.createUser(NewUserRequest.builder()
                .login("m2").email("m2@test.ru").name("M2").birthday(LocalDate.of(1991,1,1)).build());
        UserDto u3 = userService.createUser(NewUserRequest.builder()
                .login("m3").email("m3@test.ru").name("M3").birthday(LocalDate.of(1992,1,1)).build());

        userService.addFriend(u1.getId(), u3.getId());
        userService.addFriend(u2.getId(), u3.getId());

        List<UserDto> mutual = userService.getMutualFriends(u1.getId(), u2.getId());
        assertThat(mutual).extracting(UserDto::getId).containsExactly(u3.getId());
    }

    @Test
    void testGetFriends_NotFound() {
        assertThatThrownBy(() -> userService.getFriends(8888))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }
}
