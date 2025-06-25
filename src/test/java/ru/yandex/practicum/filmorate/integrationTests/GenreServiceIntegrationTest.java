package ru.yandex.practicum.filmorate.integrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class GenreServiceIntegrationTest {

    @Autowired
    private GenreService genreService;

    @Test
    void testGetAllGenres() {
        List<Genre> genres = genreService.getAll();
        assertThat(genres)
                .isNotEmpty()
                .extracting(Genre::getId)
                .contains(1, 2, 3, 4, 5);
    }

    @Test
    void testGetById_Success() {
        Genre genre = genreService.getById(1);
        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1);
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    void testGetById_NotFound() {
        assertThatThrownBy(() -> genreService.getById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Жанр не найден");
    }
}
