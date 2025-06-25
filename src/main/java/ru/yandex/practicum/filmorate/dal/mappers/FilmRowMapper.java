package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));

        java.sql.Date date = rs.getDate("release_date");
        if (date != null) {
            film.setReleaseDate(date.toLocalDate());
        }

        int minutes = rs.getInt("duration");
        film.setDuration(Duration.ofMinutes(minutes));

        film.setMpa(new MpaRating(rs.getInt("mpa_id"), null));

        return film;
    }
}
