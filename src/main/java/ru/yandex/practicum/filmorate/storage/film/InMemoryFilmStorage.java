package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Map<Integer, Film> films = new HashMap<>();

    public Collection<Film> findAll() {
        log.info("Стартовал метод findAll");
        return films.values();
    }

    public Film getFilm(Integer id) {
        return films.get(id);
    }

    public Film create(Film film) {
        log.info("Стартовал метод create с данными: {}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм создан с id: {}", film.getId());
        return film;
    }

    public Film update(Film newFilm) {
        log.info("Стартовал метод update с данными: {}", newFilm);

        if (newFilm.getId() == null || !films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильм с таким id не найден");
        }

        films.put(newFilm.getId(), newFilm);
        log.info("Фильм обновлен с id: {}", newFilm.getId());
        return newFilm;
    }

    private int getNextId() {
        log.info("Стартовал метод getNextId");
        int currentMaxId = films.keySet().stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        log.info("Возвращается следующий id: {}", currentMaxId + 1);
        return ++currentMaxId;
    }
}
