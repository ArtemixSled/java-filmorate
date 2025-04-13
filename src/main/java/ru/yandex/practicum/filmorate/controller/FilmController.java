package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.List;
import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;
    private final UserService userService;

    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
        getRequiredFilm(newFilm.getId());
        return filmService.updateFilm(newFilm);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        return getRequiredFilm(id);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable int userId, @PathVariable int filmId) {
        getRequiredUser(userId);
        getRequiredFilm(filmId);
        return filmService.addLike(userId, filmId);
    }


    @DeleteMapping("/{filmId}/like/{userId}")
    public Film deleteLike(@PathVariable int userId, @PathVariable int filmId) {
        getRequiredUser(userId);
        getRequiredFilm(filmId);
        return filmService.deleteLike(userId, filmId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") int countTop) {
        return filmService.getPopularFilms(countTop);
    }

    private User getRequiredUser(int userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("Не найден пользователь с id: " + userId);
        }
        return user;
    }

    private Film getRequiredFilm(int filmId) {
        Film film = filmService.getFilmById(filmId);
        if (film == null) {
            throw new ResourceNotFoundException("Не найден фильм с id: " + filmId);
        }
        return film;
    }

}
