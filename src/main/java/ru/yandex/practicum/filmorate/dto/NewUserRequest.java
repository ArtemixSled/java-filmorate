package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class NewUserRequest {
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^(?!.*\\s).*$", message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна быть в правильном формате")
    private String email;

    @NotNull(message = "Дата рождения не может быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}