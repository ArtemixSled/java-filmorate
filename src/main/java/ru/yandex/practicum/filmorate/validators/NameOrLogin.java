package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NameOrLoginValidator.class)
public @interface NameOrLogin {

    String message() default "Имя не может быть пустым, если логин не пустой";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
