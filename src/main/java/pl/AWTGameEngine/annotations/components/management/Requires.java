package pl.AWTGameEngine.annotations.components.management;

import pl.AWTGameEngine.components.base.ObjectComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a class, which points which component is required
 * for this component to be added.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Requires {

    Class<? extends ObjectComponent> value();

}
