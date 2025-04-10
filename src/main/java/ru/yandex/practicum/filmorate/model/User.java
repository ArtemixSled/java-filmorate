package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import jakarta.validation.constraints.*;

@Data
public class User {

    private Integer id;

    private Set<Integer> friends = new HashSet<>();

    @Pattern(regexp = "^(?!.*\\s).*$", message = "Логин не может содержать пробелы")
    private String login;

    @NotNull(groups = Creation.class)
    @NotBlank(message = "Электронная почта не может быть пустой", groups = Creation.class)
    @Email(message = "Электронная почта должна быть в правильном формате", groups = Creation.class)
    private String email;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем", groups = Creation.class)
    private LocalDate birthday;

    public interface Creation {}

    public boolean isEmailValidForUpdate() {
        if (this.email == null || this.email.trim().isEmpty()) {
            return true;
        }
        return this.email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public Set<Integer> getFriends() {
        return friends;
    }
}
