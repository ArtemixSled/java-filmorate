package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRatingRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRatingRepository mpaRatingRepository;
    private final UserRepository userRepository;
    private final FilmMapper filmMapper;

    @Transactional
    public FilmDto createFilm(NewFilmRequest request) {
        Film model = filmMapper.toModel(request);
        Film saved = filmRepository.save(model);
        if (!model.getGenres().isEmpty()) {
            genreRepository.linkGenres(saved.getId(),
                    model.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toList()));
        }
        return toDtoWithDetails(saved.getId());
    }

    @Transactional
    public FilmDto updateFilm(UpdateFilmRequest request) {
        Film existing = filmRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден: " + request.getId()));
        filmMapper.updateFromRequest(request, existing);
        filmRepository.update(existing);
        genreRepository.deleteByFilmId(existing.getId());
        if (!existing.getGenres().isEmpty()) {
            genreRepository.linkGenres(existing.getId(),
                    existing.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toList()));
        }
        return toDtoWithDetails(existing.getId());
    }

    @Transactional(readOnly = true)
    public List<FilmDto> findAll() {
        return filmRepository.findAll().stream()
                .map(f -> toDtoWithDetails(f.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FilmDto getFilmById(int id) {
        // выбросим 404, если нет
        filmRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: " + id));
        return toDtoWithDetails(id);
    }

    @Transactional
    public void addLike(int filmId, int userId) {
        filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: " + filmId));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
        filmRepository.addLike(filmId, userId);
    }

    @Transactional
    public void removeLike(int filmId, int userId) {
        filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: " + filmId));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
        filmRepository.removeLike(filmId, userId);
    }

    @Transactional(readOnly = true)
    public List<FilmDto> getPopularFilms(int count) {
        return filmRepository.findPopularIds(count).stream()
                .map(this::toDtoWithDetails)
                .collect(Collectors.toList());
    }

    private FilmDto toDtoWithDetails(int id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: " + id));

        MpaRating mpa = mpaRatingRepository.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("MPA рейтинг не найден: " + film.getMpa().getId()));
        film.setMpa(mpa);

        List<Genre> genreList = filmRepository.findGenreIdsByFilmId(id).stream()
                .map(genreId -> genreRepository.findById(genreId)
                        .orElseThrow(() -> new NotFoundException("Жанр не найден для фильма: " + id)))
                .collect(Collectors.toList());
        film.setGenres(new LinkedHashSet<>(genreList));

        Set<User> likes = filmRepository.findUserIdsByFilmId(id).stream()
                .map(userRepository::findById)
                .map(opt -> opt.orElseThrow(() -> new NotFoundException("Пользователь не найден")))
                .collect(Collectors.toSet());
        film.setLikes(likes);

        return filmMapper.toDto(film);
    }
}
