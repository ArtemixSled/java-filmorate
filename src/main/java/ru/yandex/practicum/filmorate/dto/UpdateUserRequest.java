package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Builder
@Data
public class UpdateUserRequest {
    @NotNull(message = "ID не может быть пустым")
    private Integer id;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^(?!.*\\s).*$", message = "Логин не может содержать пробелы")
    private String login;

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна быть в правильном формате")
    private String email;

    @NotNull(message = "Дата рождения не может быть пустой")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
