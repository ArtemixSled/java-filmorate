package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MpaRatingDto {

    @NotNull(message = "MPA.id не может быть null")
    private Integer id;
    private String name;

    public MpaRatingDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
