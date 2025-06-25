package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GenreDto {

    private Integer id;
    private String name;

    public GenreDto() { }

    public GenreDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
