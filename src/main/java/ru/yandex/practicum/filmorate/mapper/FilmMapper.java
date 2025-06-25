package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilmMapper {

    private final MpaRatingService mpaRatingService;
    private final GenreService genreService;

    public FilmMapper(MpaRatingService mpaRatingService,
                      GenreService genreService) {
        this.mpaRatingService = mpaRatingService;
        this.genreService = genreService;
    }

    public Film toModel(NewFilmRequest request) {
        // валидация обязательного поля mpa
        if (request.getMpa() == null || request.getMpa().getId() == null) {
            throw new IllegalArgumentException("MPA рейтинг и его id обязателен");
        }

        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(Duration.ofMinutes(request.getDuration()));

        // MPA (гарантированно не null)
        MpaRating mpa = mpaRatingService.getById(request.getMpa().getId());
        film.setMpa(mpa);

        // Жанры: если нет поля в запросе — пустой список; сохраняем порядок через LinkedHashSet
        List<GenreDto> genreDtos = request.getGenres() != null
                ? request.getGenres()
                : Collections.emptyList();
        film.setGenres(
                genreDtos.stream()
                        .map(gdto -> genreService.getById(gdto.getId()))
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );

        return film;
    }

    public FilmDto toDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration((int) film.getDuration().toMinutes());

        MpaRating mpa = film.getMpa();
        MpaRatingDto mpaDto = new MpaRatingDto(mpa.getId(), mpa.getName());
        dto.setMpa(mpaDto);

        List<GenreDto> genres = film.getGenres().stream()
                .map(g -> new GenreDto(g.getId(), g.getName()))
                .collect(Collectors.toList());
        dto.setGenres(genres);

        dto.setRate(film.getLikes().size());

        return dto;
    }

    public Film updateFromRequest(UpdateFilmRequest request, Film film) {

        if (request.getName() != null) {
            film.setName(request.getName());
        }
        if (request.getDescription() != null) {
            film.setDescription(request.getDescription());
        }
        if (request.getReleaseDate() != null) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.getDuration() != null) {
            film.setDuration(Duration.ofMinutes(request.getDuration()));
        }
        if (request.getMpa() != null && request.getMpa().getId() != null) {
            film.setMpa(mpaRatingService.getById(request.getMpa().getId()));
        }
        if (request.getGenres() != null) {
            film.setGenres(
                    request.getGenres().stream()
                            .map(gdto -> genreService.getById(gdto.getId()))
                            .collect(Collectors.toCollection(LinkedHashSet::new))
            );
        }
        return film;
    }
}
