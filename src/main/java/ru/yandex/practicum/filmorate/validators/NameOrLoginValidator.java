package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.model.User;

public class NameOrLoginValidator implements ConstraintValidator<NameOrLogin, User> {

    @Override
    public void initialize(NameOrLogin constraintAnnotation) {
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        if (user.getName() == null || user.getName().isBlank()) {
            return user.getLogin() != null && !user.getLogin().isBlank();
        }
        return true;
    }
}
