package pl.AWTGameEngine.engine.deserializers;

import pl.AWTGameEngine.components.base.ObjectComponent;

import java.lang.reflect.Method;

public interface ParameterTypeHandler {

    boolean equalsTypeClass(Class<?> type);
    void invoke(Method method, ObjectComponent component, String value) throws Exception;

}
