package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        log.info("Стартовал метод findAll");
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilm(id);
    }

    public Film addLike(int userId, int filmId) {
        User user = userStorage.findAll().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Film film = filmStorage.findAll().stream()
                .filter(u -> u.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));

        if (film.getLikes().contains(user)) {
            log.info("Пользователь с id {} уже поставил лайк фильму с id {}", user.getId(), filmId);
            return film;
        }

        film.getLikes().add(user);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", user.getId(), filmId);

        return film;
    }

    public Film deleteLike(int userId, int filmId) {
        User user = userStorage.findAll().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Film film = filmStorage.findAll().stream()
                .filter(u -> u.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));

        if (!film.getLikes().contains(user)) {
            log.info("Пользователь с id {} не ставил лайк фильму с id {}", user.getId(), filmId);
            return film;
        }

        film.getLikes().remove(user);
        log.info("Пользователь с id {} удалил лайк с фильма с id {}", user.getId(), filmId);

        return film;
    }

    public List<Film> getPopularFilms(int countTop) {
        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(countTop)
                .collect(Collectors.toList());
    }
}
