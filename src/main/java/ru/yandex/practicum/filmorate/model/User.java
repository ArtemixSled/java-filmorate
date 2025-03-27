package ru.yandex.practicum.filmorate.model;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

@Data
public class User {

    private Integer id;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^(?!.*\\s).*$", message = "Логин не может содержать пробелы")
    private String login;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна быть в правильном формате")
    private String email;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

}
