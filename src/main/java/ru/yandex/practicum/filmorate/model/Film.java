package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.Duration;
import java.time.Instant;

/**
 * Film.
 */

@Getter
@Setter
@Data
public class Film {

    private Integer id;
    private String name;
    private String description;
    private Instant releaseDate;
    private Duration duration;

}
