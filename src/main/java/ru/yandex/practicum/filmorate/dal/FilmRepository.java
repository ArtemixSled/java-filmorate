package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
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
import java.sql.Statement;
import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FilmRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<Film> mapper;
    private final GenreRepository genreRepository;
    private final MpaRatingRepository mpaRatingRepository;// исправлено
    private final FilmLikeRepository filmLikeRepository;
    private final UserRepository userRepository;

    private static final String FIND_ALL_SQL =
            "SELECT film_id AS id, name, description, release_date, duration, mpa_id FROM films";  // :contentReference[oaicite:0]{index=0}
    private static final String FIND_BY_ID_SQL =
            "SELECT film_id AS id, name, description, release_date, duration, mpa_id FROM films WHERE film_id = ?";  // :contentReference[oaicite:1]{index=1}
    private static final String INSERT_SQL =
            "INSERT INTO films(name, description, release_date, duration, mpa_id) VALUES(?, ?, ?, ?, ?)";  // :contentReference[oaicite:2]{index=2}
    private static final String UPDATE_SQL =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";  // :contentReference[oaicite:3]{index=3}
    private static final String DELETE_SQL =
            "DELETE FROM films WHERE film_id = ?";

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
                ps.setNull(5, Types.INTEGER);
            }

            return ps;
        }, keyHolder);

        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        film.setId(id);

        if (!film.getGenres().isEmpty()) {
            genreRepository.linkGenres(id,
                    film.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toList())
            );
        }
        return film;
    }

    public Film update(Film film) {
        int updated = jdbc.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(UPDATE_SQL);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration().toMinutes());

            if (film.getMpa() != null && film.getMpa().getId() != null) {
                ps.setInt(5, film.getMpa().getId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setInt(6, film.getId());
            return ps;
        });
        if (updated == 0) {
            throw new NotFoundException("Фильм не найден для обновления: " + film.getId());
        }

        genreRepository.deleteByFilmId(film.getId());
        if (!film.getGenres().isEmpty()) {
            genreRepository.linkGenres(film.getId(),
                    film.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toList())
            );
        }

        return film;
    }

    public Optional<Film> findById(Integer id) {
        return jdbc.query(FIND_BY_ID_SQL, mapper, id)
                .stream()
                .findAny()
                .map(this::loadDetails);
    }

    public List<Film> findAll() {
        return jdbc.query(FIND_ALL_SQL, mapper).stream()
                .map(this::loadDetails)
                .collect(Collectors.toList());
    }

    public boolean delete(Integer id) {
        return jdbc.update(DELETE_SQL, id) > 0;
    }

    public void addLike(Integer filmId, Integer userId) {
        filmLikeRepository.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        filmLikeRepository.removeLike(filmId, userId);
    }

    public List<Film> findPopular(int count) {
        String sql =
                "SELECT f.film_id AS id, f.name, f.description, f.release_date, f.duration, f.mpa_id "+
                        "FROM films f "+
                        "LEFT JOIN likes l ON f.film_id = l.film_id "+
                        "GROUP BY f.film_id "+
                        "ORDER BY COUNT(l.user_id) DESC "+
                        "LIMIT ?";
        return jdbc.query(sql, mapper, count).stream()
                .map(this::loadDetails)
                .collect(Collectors.toList());
    }

    private Film loadDetails(Film film) {
        MpaRating mpa = mpaRatingRepository.findById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("MPA рейтинг не найден: " + film.getMpa().getId()));
        film.setMpa(mpa);

        Set<Genre> genres = genreRepository.findGenreIdsByFilmId(film.getId()).stream()
                .map(genreRepository::findById)
                .map(opt -> opt.orElseThrow(() -> new NotFoundException("Жанр не найден: " + film.getId())))
                .collect(Collectors.toSet());
        film.setGenres(genres);

        Set<User> likes = filmLikeRepository.findUserIdsByFilmId(film.getId()).stream()
                .map(userRepository::findById)
                .map(opt -> opt.orElseThrow(() -> new NotFoundException("Пользователь не найден")))
                .collect(Collectors.toSet());
        film.setLikes(likes);

        return film;
    }
}
