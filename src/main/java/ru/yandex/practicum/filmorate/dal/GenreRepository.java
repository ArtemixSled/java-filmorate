package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class GenreRepository extends BaseRepository<Genre> {

    private static final String FIND_ALL =
            "SELECT genre_id AS id, name FROM genres";
    private static final String FIND_BY_ID =
            "SELECT genre_id AS id, name FROM genres WHERE genre_id = ?";
    private static final String LINK_GENRE_SQL =
            "INSERT INTO film_genres(film_id, genre_id) VALUES(?, ?)";
    private static final String DELETE_BY_FILM_SQL =
            "DELETE FROM film_genres WHERE film_id = ?";
    private static final String FIND_IDS_BY_FILM_SQL =
            "SELECT genre_id FROM film_genres WHERE film_id = ?";

    public GenreRepository(JdbcTemplate jdbc) {
        super(jdbc, Genre.class);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL);
    }

    public Optional<Genre> findById(Integer id) {
        return findOne(FIND_BY_ID, id);
    }

    public void linkGenres(Integer filmId, List<Integer> genreIds) {
        if (genreIds == null) return;
        List<Integer> nonNullIds = genreIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (nonNullIds.isEmpty()) return;

        jdbc.batchUpdate(
                LINK_GENRE_SQL,
                nonNullIds,
                nonNullIds.size(),
                (ps, genreId) -> {
                    ps.setInt(1, filmId);
                    ps.setInt(2, genreId);
                }
        );
    }

    public void deleteByFilmId(Integer filmId) {
        jdbc.update(DELETE_BY_FILM_SQL, filmId);
    }

    public List<Integer> findGenreIdsByFilmId(Integer filmId) {
        return jdbc.queryForList(FIND_IDS_BY_FILM_SQL, Integer.class, filmId);
    }
}
