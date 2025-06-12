package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.DurationDeserializer;
import ru.yandex.practicum.filmorate.DurationSerializer;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import jakarta.validation.constraints.*;
import ru.yandex.practicum.filmorate.validators.ValidDuration;
import ru.yandex.practicum.filmorate.validators.ValidReleaseDate;

@Getter
@Setter
@Data
public class Film {

    private Integer id;

    private Set<Genre> genres = new HashSet<>();

    private MpaRating MPA;

    private Set<User> likes = new HashSet<>();

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @ValidReleaseDate
    private LocalDate releaseDate;

    @ValidDuration(message = "Продолжительность фильма должна быть положительным числом")
    @JsonDeserialize(using = DurationDeserializer.class)
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;
}
