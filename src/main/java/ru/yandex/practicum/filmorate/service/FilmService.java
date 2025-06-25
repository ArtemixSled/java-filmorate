package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final FilmMapper filmMapper;

    @Autowired
    public FilmService(FilmRepository filmRepository,
                       UserRepository userRepository,
                       FilmMapper filmMapper) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.filmMapper = filmMapper;
    }

    public FilmDto createFilm(NewFilmRequest filmRequest) {
        Film film = filmMapper.toModel(filmRequest);
        Film saved = filmRepository.save(film);
        return filmMapper.toDto(saved);
    }

    public FilmDto updateFilm(UpdateFilmRequest filmRequest) {
        Film existing = filmRepository.findById(filmRequest.getId())
                .orElseThrow(() -> new NotFoundException(
                        "Фильм не найден: " + filmRequest.getId()));

        filmMapper.updateFromRequest(filmRequest, existing);

        Film updated = filmRepository.update(existing);

        return filmMapper.toDto(updated);
    }

    public List<FilmDto> findAll() {
        return filmRepository.findAll().stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }

    public FilmDto getFilmById(int id) {
        Film film = filmRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: " + id));
        return filmMapper.toDto(film);
    }

    public void addLike(int filmId, int userId) {
        filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: " + filmId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
        filmRepository.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден: " + filmId));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
        filmRepository.removeLike(filmId, userId);
    }

    public List<FilmDto> getPopularFilms(int count) {
        return filmRepository.findPopular(count).stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }
}
