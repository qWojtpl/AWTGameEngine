package pl.AWTGameEngine.objects;

import pl.AWTGameEngine.annotations.BindingGetter;
import pl.AWTGameEngine.annotations.BindingSetter;
import pl.AWTGameEngine.annotations.SerializationGetter;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.engine.BindingsManager;
import pl.AWTGameEngine.engine.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class BindableProperty {

    private final Object[] objects = new Object[2];
    private final Method[] methods = new Method[2];

    public BindableProperty(Object object1, Object object2, String serializationField1, String serializationField2) {
        objects[0] = object1;
        objects[1] = object2;
        try {
            methods[0] = getMethodClass(object1, serializationField1, true);
            methods[1] = getMethodClass(object2, serializationField2, false);
            BindingsManager.addBindableProperty(this);
        } catch(Exception e) {
            Logger.log("Cannot bind properties: " + getConnectionString(), e);
        }
    }

    private Method getMethodClass(Object object, String field, boolean isGetter) throws Exception {
        String fieldName = field.substring(0, 1).toUpperCase() + field.substring(1);
        Method method;
        if(isGetter) {
            method = object.getClass().getMethod("get" + fieldName);
            if(!hasBindingAnnotation(method, true)) {
                throw new Exception("Method doesn't have BindingGetter or SerializationGetter annotation.");
            }
        } else {
            method = object.getClass().getMethod("set" + fieldName, String.class);
            if(!hasBindingAnnotation(method, false)) {
                throw new Exception("Method doesn't have BindingSetter or SerializationSetter annotation.");
            }
        }
        return method;
    }

    private boolean hasBindingAnnotation(Method method, boolean getterAnnotation) {
        if(getterAnnotation) {
            return method.isAnnotationPresent(BindingGetter.class) || method.isAnnotationPresent(SerializationGetter.class);
        }
        return method.isAnnotationPresent(BindingSetter.class) || method.isAnnotationPresent(SerializationSetter.class);
    }

    public String getConnectionString() {
        return objects[0] + ":" + methods[0].getName() +
                " <-> " +
                objects[1] + ":" + methods[1].getName();
    }

    public Object[] getObjects() {
        return this.objects.clone();
    }

    public Method[] getMethods() {
        return this.methods.clone();
    }

}
