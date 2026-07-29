package pl.AWTGameEngine.annotations.methods;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods which are annotated as {@link EventMethod} will be available in {@link pl.AWTGameEngine.engine.EventHandler},
 * but only if they are overridden. For example, class {@link pl.AWTGameEngine.components.base.ObjectComponent}
 * have many methods annotated as EventMethod, but only methods annotated as {@link Override} will be called from EventHandler.
 * If you want to create custom events, create a class (which inherits from ObjectComponent), write your events, annotate
 * them as {@link EventMethod}, and make your component inherit from that class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EventMethod {
}
