package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    void createFilmValidData() {
        Film film = new Film();
        film.setName("Film Title");
        film.setDescription("This is a description of the film.");
        film.setReleaseDate(Instant.parse("2025-03-25T15:30:00Z"));
        film.setDuration(Duration.ofHours(2).plusMinutes(30));

        Film createdFilm = filmController.create(film);

        assertNotNull(createdFilm);
        assertEquals(film.getName(), createdFilm.getName());
        assertEquals(film.getDescription(), createdFilm.getDescription());
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
        assertEquals(film.getDuration(), createdFilm.getDuration());
    }

    @Test
    void createFilmWithTooLongDescription() {
        Film film = new Film();
        film.setName("Film Title");
        film.setDescription("A".repeat(201));  // Too long description
        film.setReleaseDate(Instant.parse("2025-03-25T15:30:00Z"));
        film.setDuration(Duration.ofHours(2).plusMinutes(30));

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class, () -> filmController.create(film));
        assertEquals("максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    void createFilmWithInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Film Title");
        film.setDescription("This is a description of the film.");
        film.setReleaseDate(Instant.parse("1800-01-01T00:00:00Z"));
        film.setDuration(Duration.ofHours(2).plusMinutes(30));

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class, () -> filmController.create(film));
        assertEquals("дата релиза должна быть — не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void createFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Film Title");
        film.setDescription("This is a description of the film.");
        film.setReleaseDate(Instant.parse("2025-03-25T15:30:00Z"));
        film.setDuration(Duration.ofMinutes(-90));  // Invalid duration

        ConditionsNotMetException exception = assertThrows(ConditionsNotMetException.class, () -> filmController.create(film));
        assertEquals("продолжительность фильма должна быть положительным числом", exception.getMessage());
    }

    @Test
    void findAllFilms() {
        Film film1 = new Film();
        film1.setName("Film Title 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(Instant.parse("2025-03-25T15:30:00Z"));
        film1.setDuration(Duration.ofHours(2));

        Film film2 = new Film();
        film2.setName("Film Title 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(Instant.parse("2025-03-26T15:30:00Z"));
        film2.setDuration(Duration.ofHours(1));

        filmController.create(film1);
        filmController.create(film2);

        Collection<Film> films = filmController.findAll();

        assertEquals(2, films.size());
    }
}
