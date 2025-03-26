package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private Map<Integer, Film> films = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private static final int MAX_DESCRIPTION_LENGTH = 200;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Стартовал метод findAll");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Стартовал метод create с данными: {}", film);

        if (film.getName() == null || film.getName().isBlank()) {
            String errorMessage = "название не может быть пустым";
            log.error(errorMessage);
            throw new ConditionsNotMetException(errorMessage);
        }
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            String errorMessage = "максимальная длина описания — 200 символов";
            log.error(errorMessage);
            throw new ConditionsNotMetException(errorMessage);
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            String errorMessage = "дата релиза должна быть — не раньше 28 декабря 1895 года";
            log.error(errorMessage);
            throw new ConditionsNotMetException(errorMessage);
        }
        if (film.getDuration().isNegative()) {
            String errorMessage = "продолжительность фильма должна быть положительным числом";
            log.error(errorMessage);
            throw new ConditionsNotMetException(errorMessage);
        }
        film.setId(getNextId());
        log.info("Состояние фильмов до создания: {}", films);
        films.put(film.getId(), film);
        log.info("Фильм создан с id: {}", film.getId());
        log.info("Состояние фильмов после создания: {}", films);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Стартовал метод create с данными: {}", newFilm);

        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {

            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                String errorMessage = "название не может быть пустым";
                log.error(errorMessage);
                throw new ConditionsNotMetException(errorMessage);
            }
            if (newFilm.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
                String errorMessage = "максимальная длина описания — 200 символов";
                log.error(errorMessage);
                throw new ConditionsNotMetException(errorMessage);
            }
            if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
                String errorMessage = "дата релиза должна быть — не раньше 28 декабря 1895 года";
                log.error(errorMessage);
                throw new ConditionsNotMetException(errorMessage);
            }
            if (newFilm.getDuration().isNegative()) {
                String errorMessage = "продолжительность фильма должна быть положительным числом";
                log.error(errorMessage);
                throw new ConditionsNotMetException(errorMessage);
            }
            log.info("Состояние фильмов до создания: {}", films);
            films.put(newFilm.getId(), newFilm);
            log.info("Фильм создан с id: {}", newFilm.getId());
            log.info("Состояние фильмов после создания: {}", films);
            return newFilm;
        }
        String errorMessage = "Фильм с id = " + newFilm.getId() + " не найден";
        log.error(errorMessage);
        throw new NotFoundException(errorMessage);
    }

    private int getNextId() {
        log.info("Стартовал метод getNextId");
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        log.info("Возвращается следующий id: {}", currentMaxId + 1);
        return ++currentMaxId;
    }
}
