package pl.AWTGameEngine.annotations.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ComponentMeta {

    String name();
    String description()    default "No description provided.";
    String author()         default "No author provided.";

}
