package pl.AWTGameEngine.annotations.methods;

import pl.AWTGameEngine.engine.deserializers.ParameterTypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods which are annotated with {@link FromXML} can be executed
 * by {@link pl.AWTGameEngine.engine.deserializers.XMLDeserializer}.
 * Methods which are annotated, should look like <code>setField</code>, where <code>Field</code>
 * is a name of XML field with first big letter. It can have only one argument.
 * <pre>
 * {@code
 *      @FromXML
 *      public void setSpeed(float speed) {
 *          this.speed = speed;
 *      }
 * }
 * </pre>
 * By default, only some argument types are supported (e.g. int, long, float, double,
 * boolean, String, TransformSet). You can add your own parameter type handler, using
 * {@link pl.AWTGameEngine.engine.deserializers.XMLDeserializer#addParameterTypeHandler(ParameterTypeHandler)}.<br><br>
 * If you want to pass a list instead of single number or object, check out {@link pl.AWTGameEngine.objects.lists.FloatValues},
 * {@link pl.AWTGameEngine.objects.lists.TransformSetValues} or any similar classes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FromXML {
}
