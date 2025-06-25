package ru.yandex.practicum.filmorate.integrationTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class MpaRatingServiceIntegrationTest {

    @Autowired
    private MpaRatingService mpaRatingService;

    @Test
    void testGetAllRatings() {
        List<MpaRating> ratings = mpaRatingService.getAll();
        assertThat(ratings)
                .isNotEmpty()
                .extracting(MpaRating::getId)
                .contains(1, 2, 3, 4, 5);
    }

    @Test
    void testGetById_Success() {
        MpaRating rating = mpaRatingService.getById(1);
        assertThat(rating).isNotNull();
        assertThat(rating.getId()).isEqualTo(1);
        assertThat(rating.getName()).isEqualTo("G");
    }

    @Test
    void testGetById_NotFound() {
        assertThatThrownBy(() -> mpaRatingService.getById(999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Рейтинг не найден");
    }
}
