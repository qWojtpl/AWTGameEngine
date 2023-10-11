package pl.AWTGameEngine.annotations;

import pl.AWTGameEngine.components.ObjectComponent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConflictsWith {

    Class<? extends ObjectComponent> value();

}
