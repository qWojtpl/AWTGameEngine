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

    private final Object owner;
    private final Object[] objects = new Object[2];
    private final Method[] methods = new Method[2];

    public BindableProperty(Object owner, Object from, String fromField, Object to, String toField) {
        this.owner = owner;
        objects[0] = from;
        objects[1] = to;
        try {
            methods[0] = getMethodClass(from, fromField, true);
            methods[1] = getMethodClass(to, toField, false);
            BindingsManager.addBindableProperty(this);
        } catch(Exception e) {
            Logger.log("Cannot bind properties: " + getConnectionString(), e);
        }
    }

    private Method getMethodClass(Object object, String field, boolean isGetter) throws Exception {
        String fieldName = field.substring(0, 1).toUpperCase() + field.substring(1);
        Method method;
        if(isGetter) {
            method = object.getClass().getDeclaredMethod("get" + fieldName);
            if(!hasBindingAnnotation(method, true)) {
                throw new Exception("Method doesn't have BindingGetter or SerializationGetter annotation.");
            }
        } else {
            method = object.getClass().getDeclaredMethod("set" + fieldName, String.class);
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

    public Object getOwner() {
        return this.owner;
    }

    public Object[] getObjects() {
        return this.objects.clone();
    }

    public Method[] getMethods() {
        return this.methods.clone();
    }

}
