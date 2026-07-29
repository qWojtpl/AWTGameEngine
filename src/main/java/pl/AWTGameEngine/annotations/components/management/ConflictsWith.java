package pl.AWTGameEngine.annotations.components.management;

import pl.AWTGameEngine.components.base.ObjectComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a class, which points which components conflicts with this component,
 * and cannot be in the same object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ConflictsWith {

    Class<? extends ObjectComponent> value();

}
