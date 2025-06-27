package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.MpaRatingRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@Service
public class MpaRatingService {
    private final MpaRatingRepository repo;

    public MpaRatingService(MpaRatingRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<MpaRating> getAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public MpaRating getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг не найден: " + id));
    }
}
