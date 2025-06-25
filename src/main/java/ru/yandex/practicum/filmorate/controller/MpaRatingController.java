package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaRatingController {
    private final MpaRatingService service;

    public MpaRatingController(MpaRatingService service) {
        this.service = service;
    }

    @GetMapping
    public List<MpaRating> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MpaRating getOne(@PathVariable Integer id) {
        return service.getById(id);
    }
}
