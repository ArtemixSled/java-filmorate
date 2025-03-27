package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
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
        film.setReleaseDate(LocalDate.of(2025, Month.DECEMBER, 28));
        film.setDuration(Duration.ofHours(2).plusMinutes(30));

        BindingResult bindingResult;
        Film createdFilm = filmController.create(film);

        assertNotNull(createdFilm);
        assertEquals(film.getName(), createdFilm.getName());
        assertEquals(film.getDescription(), createdFilm.getDescription());
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
        assertEquals(film.getDuration(), createdFilm.getDuration());
    }

    @Test
    void findAllFilms() {
        Film film1 = new Film();
        film1.setName("Film Title 1");
        film1.setDescription("Description 1");
        film1.setReleaseDate(LocalDate.of(2025, Month.DECEMBER, 28));
        film1.setDuration(Duration.ofHours(2));

        Film film2 = new Film();
        film2.setName("Film Title 2");
        film2.setDescription("Description 2");
        film2.setReleaseDate(LocalDate.of(2025, Month.DECEMBER, 28));
        film2.setDuration(Duration.ofHours(1));

        filmController.create(film1);
        filmController.create(film2);

        Collection<Film> films = filmController.findAll();

        assertEquals(2, films.size());
    }
}
