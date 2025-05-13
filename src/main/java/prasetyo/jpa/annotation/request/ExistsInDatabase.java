package prasetyo.jpa.annotation.request;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import prasetyo.jpa.validator.ExistsInDatabaseValidator;

@Documented
@Constraint(validatedBy = ExistsInDatabaseValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistsInDatabase {

    String message() default "Field not found";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // Nama entity dan nama field yang akan dicek
    Class<?> entity();

    String field() default "id";
}
