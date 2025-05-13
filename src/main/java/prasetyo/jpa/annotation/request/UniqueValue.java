package prasetyo.jpa.annotation.request;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import prasetyo.jpa.validator.UniqueValueValidator;

@Documented
@Constraint(validatedBy = UniqueValueValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueValue {

    String message() default "Data already exits";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> entity();

    String field() default "id";

    /**
     * Nama field ID yang bisa dikecualikan (mirip except di Laravel)
     * Biasanya "id"
     */
    String exceptIdField() default "";

    /**
     * Ambil value except id-nya dari mana? (biasanya dari request lain)
     */
    String exceptIdSourceField() default "";
}
