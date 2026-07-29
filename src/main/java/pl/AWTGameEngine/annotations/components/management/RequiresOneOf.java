package pl.AWTGameEngine.annotations.components.management;

import pl.AWTGameEngine.components.base.ObjectComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a class, points a list of component, and at least one of them
 * is required for component to be added.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RequiresOneOf {

    Class<? extends ObjectComponent>[] value();

}
