package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
public class GenreService {
    private final GenreRepository repo;

    public GenreService(GenreRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Genre> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Genre getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр не найден: " + id));
    }
}
