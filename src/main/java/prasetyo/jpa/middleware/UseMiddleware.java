package prasetyo.jpa.middleware;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UseMiddleware {
    String[] names(); // contoh: {"auth", "roles:admin,user"}
}