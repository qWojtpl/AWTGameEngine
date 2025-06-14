package pl.AWTGameEngine.annotations;

import pl.AWTGameEngine.components.base.ObjectComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Requires {

    Class<? extends ObjectComponent> value();

}
