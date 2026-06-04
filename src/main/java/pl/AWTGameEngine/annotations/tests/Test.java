package pl.AWTGameEngine.annotations.tests;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Repeatable(Tests.class)
public @interface Test {

    String name();
    String testClass();

}
