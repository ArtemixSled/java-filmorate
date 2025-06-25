package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRatingRepository extends BaseRepository<MpaRating> {

    private static final String FIND_ALL =
            "SELECT mpa_id AS id, name FROM mpa_rating";
    private static final String FIND_BY_ID =
            "SELECT mpa_id AS id, name FROM mpa_rating WHERE mpa_id = ?";

    public MpaRatingRepository(JdbcTemplate jdbc) {
        super(jdbc, MpaRating.class);
    }

    public List<MpaRating> findAll() {
        return findMany(FIND_ALL);
    }

    public Optional<MpaRating> findById(Integer id) {
        return findOne(FIND_BY_ID, id);
    }
}
