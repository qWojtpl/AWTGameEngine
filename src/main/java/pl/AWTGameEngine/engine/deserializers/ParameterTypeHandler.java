package pl.AWTGameEngine.engine.deserializers;

import java.lang.reflect.Method;

public interface ParameterTypeHandler {

    boolean equalsTypeClass(Class<?> type);
    void invoke(Method method, Object object, String value) throws Exception;

}
