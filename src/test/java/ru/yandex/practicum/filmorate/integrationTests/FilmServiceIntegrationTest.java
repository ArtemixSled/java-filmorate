package ru.yandex.practicum.filmorate.integrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class FilmServiceIntegrationTest {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Test
    void testCreateFilm_Success() {
        NewFilmRequest req = NewFilmRequest.builder()
                .name("Test Film")
                .description("A test description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(MpaRatingDto.builder().id(1).build())
                .genres(Arrays.asList(
                        GenreDto.builder().id(1).build(),
                        GenreDto.builder().id(2).build()
                ))
                .build();

        FilmDto dto = filmService.createFilm(req);

        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getName()).isEqualTo("Test Film");
        assertThat(dto.getDescription()).isEqualTo("A test description");
        assertThat(dto.getReleaseDate()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(dto.getDuration()).isEqualTo(120);
        assertThat(dto.getMpa().getId()).isEqualTo(1);
        assertThat(dto.getGenres()).hasSize(2);
    }

    @Test
    void testGetFilmById_NotFound() {
        assertThatThrownBy(() -> filmService.getFilmById(9999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Фильм не найден");
    }

    @Test
    void testUpdateFilm_Success() {
        NewFilmRequest createReq = NewFilmRequest.builder()
                .name("Original")
                .description("Desc")
                .releaseDate(LocalDate.of(1999, 12, 31))
                .duration(100)
                .mpa(MpaRatingDto.builder().id(2).build())
                .genres(Arrays.asList(GenreDto.builder().id(3).build()))
                .build();
        FilmDto created = filmService.createFilm(createReq);

        UpdateFilmRequest updateReq = UpdateFilmRequest.builder()
                .id(created.getId())
                .name("Updated")
                .description("Updated Desc")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(150)
                .mpa(MpaRatingDto.builder().id(3).build())
                .genres(Arrays.asList(
                        GenreDto.builder().id(2).build(),
                        GenreDto.builder().id(4).build()
                ))
                .build();
        FilmDto updated = filmService.updateFilm(updateReq);

        assertThat(updated.getName()).isEqualTo("Updated");
        assertThat(updated.getDescription()).isEqualTo("Updated Desc");
        assertThat(updated.getReleaseDate()).isEqualTo(LocalDate.of(2001, 1, 1));
        assertThat(updated.getDuration()).isEqualTo(150);
        assertThat(updated.getMpa().getId()).isEqualTo(3);
        assertThat(updated.getGenres()).hasSize(2);
    }

    @Test
    void testFindAllFilms() {
        filmService.createFilm(NewFilmRequest.builder()
                .name("F1").description("D1").releaseDate(LocalDate.of(2005,5,5)).duration(90)
                .mpa(MpaRatingDto.builder().id(1).build())
                .genres(Arrays.asList(GenreDto.builder().id(1).build()))
                .build());
        filmService.createFilm(NewFilmRequest.builder()
                .name("F2").description("D2").releaseDate(LocalDate.of(2006,6,6)).duration(110)
                .mpa(MpaRatingDto.builder().id(2).build())
                .genres(Arrays.asList(GenreDto.builder().id(2).build()))
                .build());

        List<FilmDto> all = filmService.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void testAddAndRemoveLike() {
        UserDto user = userService.createUser(NewUserRequest.builder()
                .login("liker").email("like@test.ru").name("Liker").birthday(LocalDate.of(1995,1,1))
                .build());
        NewFilmRequest filmReq = NewFilmRequest.builder()
                .name("Like Film").description("Desc").releaseDate(LocalDate.of(2010,1,1)).duration(100)
                .mpa(MpaRatingDto.builder().id(1).build())
                .genres(Arrays.asList(GenreDto.builder().id(1).build()))
                .build();
        FilmDto film = filmService.createFilm(filmReq);

        filmService.addLike(film.getId(), user.getId());
        List<FilmDto> popular = filmService.getPopularFilms(1);
        assertThat(popular).hasSize(1);
        assertThat(popular.get(0).getId()).isEqualTo(film.getId());
        assertThat(popular.get(0).getRate()).isEqualTo(1);

        filmService.removeLike(film.getId(), user.getId());
        List<FilmDto> afterRemoval = filmService.getPopularFilms(1);
        assertThat(afterRemoval).hasSize(1);
        assertThat(afterRemoval.get(0).getId()).isEqualTo(film.getId());
        assertThat(afterRemoval.get(0).getRate()).isEqualTo(0);
    }
}
