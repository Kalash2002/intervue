package kit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a test method. Tests run in ascending {@code order}; each gets its own instance of the test class. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
    int order() default 0;

    /** The test fails with TIMEOUT if it runs longer than this. */
    long timeoutMs() default 5000;
}
