package pl.AWTGameEngine.annotations.components.management;

import java.lang.annotation.*;

/**
 * Only one component of this type can be added to the object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Unique {
}
