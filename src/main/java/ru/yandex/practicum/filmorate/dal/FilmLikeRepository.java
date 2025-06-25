package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilmLikeRepository {

    private final JdbcTemplate jdbc;

    private static final String ADD_LIKE_SQL =
            "INSERT INTO likes(film_id, user_id) VALUES(?, ?)";
    private static final String REMOVE_LIKE_SQL =
            "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_LIKES_SQL =
            "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String REMOVE_ALL_SQL =
            "DELETE FROM likes WHERE film_id = ?";

    public void addLike(Integer filmId, Integer userId) {
        jdbc.update(ADD_LIKE_SQL, filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        jdbc.update(REMOVE_LIKE_SQL, filmId, userId);
    }

    public List<Integer> findUserIdsByFilmId(Integer filmId) {
        return jdbc.queryForList(FIND_LIKES_SQL, Integer.class, filmId);
    }

    public void removeAllLikesByFilmId(Integer filmId) {
        jdbc.update(REMOVE_ALL_SQL, filmId);
    }
}
