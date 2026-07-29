package pl.AWTGameEngine.annotations.methods;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods which are annotated with {@link SaveState} will be fired during scene state saving from {@link pl.AWTGameEngine.scenes.SceneStateSaver}.
 * Name of the method annotated with {@link SaveState} doesn't matter, but {@link SaveState#name()} need to be the targeted XML field name.
 * Make sure to provide right field name to make {@link FromXML} work. Example with field named <code>speed</code>:
 * <pre>
 * {@code
 * @SaveState(name = "speed")
 * public float getSpeedAsFloat() {
 *     return (float) this.speed;
 * }
 *
 * @FromXML
 * public void setSpeed(float speed) {
 *     this.speed = speed;
 * }
 * }
 * </pre>
 * The output of {@link SaveState} method will be parsed using <code>toString()</code> method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SaveState {

    /**
     * XML field name
     */
    String name();

}
