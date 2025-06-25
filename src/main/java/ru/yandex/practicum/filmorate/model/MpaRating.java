package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class MpaRating {
    private Integer id;
    private String name;

    public MpaRating() {
    }

    public MpaRating(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
