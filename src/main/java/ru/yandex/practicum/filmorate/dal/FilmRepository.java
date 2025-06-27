package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmRepository {
    private final JdbcTemplate jdbc;

    private static final String INSERT_SQL =
            "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES(?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
    private static final String DELETE_SQL =
            "DELETE FROM films WHERE film_id = ?";
    private static final String SELECT_BY_ID_SQL =
            "SELECT film_id, name, description, release_date, duration, mpa_id FROM films WHERE film_id = ?";
    private static final String SELECT_ALL_SQL =
            "SELECT film_id, name, description, release_date, duration, mpa_id FROM films";
    private static final String SELECT_GENRE_IDS_SQL =
            "SELECT genre_id FROM film_genres WHERE film_id = ?";
    private static final String SELECT_LIKE_USER_IDS_SQL =
            "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String INSERT_LIKE_SQL =
            "INSERT INTO likes(film_id, user_id) VALUES(?, ?)";
    private static final String DELETE_LIKE_SQL =
            "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String SELECT_POPULAR_IDS_SQL =
            "SELECT f.film_id FROM films f LEFT JOIN likes l ON f.film_id =" +
                    "l.film_id GROUP BY f.film_id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
    private static final String SQL_WITH_ALL_DETAILS =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                    "m.mpa_id AS mpa_id, m.name AS mpa_name, " +
                    "g.genre_id AS genre_id, g.name AS genre_name, " +
                    "l.user_id  AS like_user_id FROM films f LEFT JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id ORDER BY f.film_id";
    private static final String SELECT_BY_ID_WITH_DETAILS =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                    "m.mpa_id AS mpa_id, m.name AS mpa_name, " +
                    "g.genre_id AS genre_id, g.name AS genre_name, " +
                    "l.user_id AS like_user_id " +
                    "FROM films f " +
                    "LEFT JOIN mpa_rating m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN likes l ON f.film_id = l.film_id WHERE f.film_id = ?";

    private static final RowMapper<Film> FILM_ROW_MAPPER = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(Duration.ofMinutes(rs.getLong("duration")));
        film.setMpa(new MpaRating(rs.getInt("mpa_id"), null));
        return film;
    };

    public Film save(Film film) {
        var keyHolder = new GeneratedKeyHolder();
        jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration().toMinutes());
            if (film.getMpa() != null && film.getMpa().getId() != null) {
                ps.setInt(5, film.getMpa().getId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        int id = Optional.ofNullable(keyHolder.getKey())
                .orElseThrow(() -> new RuntimeException("Не удалось получить ID фильма"))
                .intValue();
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        int count = jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(UPDATE_SQL);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration().toMinutes());
            if (film.getMpa() != null && film.getMpa().getId() != null) {
                ps.setInt(5, film.getMpa().getId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.setInt(6, film.getId());
            return ps;
        });
        if (count == 0) {
            throw new NotFoundException("Фильм не найден для обновления: " + film.getId());
        }
        return film;
    }

    public boolean delete(int id) {
        return jdbc.update(DELETE_SQL, id) > 0;
    }

    public Optional<Film> findById(int id) {
        List<Film> list = jdbc.query(SELECT_BY_ID_SQL, FILM_ROW_MAPPER, id);
        return list.stream().findFirst();
    }

    public List<Film> findAll() {
        return jdbc.query(SELECT_ALL_SQL, FILM_ROW_MAPPER);
    }

    public void addLike(int filmId, int userId) {
        jdbc.update(INSERT_LIKE_SQL, filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        jdbc.update(DELETE_LIKE_SQL, filmId, userId);
    }

    public List<Integer> findPopularIds(int count) {
        return jdbc.query(SELECT_POPULAR_IDS_SQL, (rs, i) -> rs.getInt("film_id"), count);
    }

    private final ResultSetExtractor<List<Film>> extractor = rs -> {
        Map<Integer, Film> map = new LinkedHashMap<>();
        while (rs.next()) {
            int id = rs.getInt("film_id");
            Film film = map.computeIfAbsent(id, key -> {
                Film f = new Film();
                try {
                    f.setId(key);
                    f.setName(rs.getString("name"));
                    f.setDescription(rs.getString("description"));
                    f.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    f.setDuration(Duration.ofMinutes(rs.getLong("duration")));
                    f.setMpa(new MpaRating(
                            rs.getInt("mpa_id"),
                            rs.getString("mpa_name")
                    ));
                    f.setGenres(new LinkedHashSet<>());
                    f.setLikes(new HashSet<>());
                    return f;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            int genreId = rs.getInt("genre_id");
            if (!rs.wasNull()) {
                film.getGenres().add(new Genre(
                        genreId, rs.getString("genre_name")
                ));
            }
            int userId = rs.getInt("like_user_id");
            if (!rs.wasNull()) {
                User u = new User();
                u.setId(userId);
                film.getLikes().add(u);
            }
        }
        return new ArrayList<>(map.values());
    };

    public List<Film> findAllWithDetails() {
        return jdbc.query(SQL_WITH_ALL_DETAILS, extractor);
    }

    public Optional<Film> findByIdWithDetails(int id) {
        List<Film> list = jdbc.query(SELECT_BY_ID_WITH_DETAILS, extractor, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
