package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.Statement;
import java.util.List;
import java.util.Optional;

public class BaseRepository<T> {

    protected final JdbcTemplate jdbc;
    private final Class<T> entityType;
    protected final RowMapper<T> mapper;

    public BaseRepository(JdbcTemplate jdbc, Class<T> entityType) {
        this.jdbc = jdbc;
        this.entityType = entityType;
        this.mapper = new BeanPropertyRowMapper<>(entityType);
    }

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (org.springframework.dao.EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    public boolean delete(String sql, long id) {
        int rowsDeleted = jdbc.update(sql, id);
        return rowsDeleted > 0;
    }

    protected Integer create(String sql, Object... params) {
        var keyHolder = new org.springframework.jdbc.support.GeneratedKeyHolder();
        jdbc.update(con -> {
            var ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps;
        }, keyHolder);

        var key = keyHolder.getKey();
        if (key != null) return key.intValue();
        throw new InternalServerException("Не удалось сохранить данные");
    }

    protected void update(String sql, Object... params) {
        int rows = jdbc.update(sql, params);
        if (rows == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }
}
