package pl.AWTGameEngine.engine.deserializers;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;

import java.lang.reflect.Method;
import java.util.List;

import static pl.AWTGameEngine.engine.deserializers.GameObjectDeserializer.getValue;

public abstract class XMLDeserializer {

    public static Class<? extends ObjectComponent> getClassFromTag(Node childNode, List<String> packages) throws ClassNotFoundException {
        String className = ((Element) childNode).getTagName().replace(".", "$");
        String pckg = getValue(childNode, "_package");
        Class<? extends ObjectComponent> clazz = null;
        if(!pckg.equals("0")) {
            className = pckg + "." + className;
            clazz = Class.forName(className)
                    .asSubclass(ObjectComponent.class);
        } else {
            for(String p : packages) {
                try {
                    clazz = Class.forName(p + "." + className).asSubclass(ObjectComponent.class);
                    className = p + "." + className;
                    break;
                } catch(ClassNotFoundException e) {
                    continue;
                }
            }
            if(clazz == null) {
                throw new ClassNotFoundException();
            }
        }
        return clazz;
    }

    public static void handleSetMethod(ObjectComponent component, String methodName, String value) {
        try {
            boolean found = false;
            for(Method method : component.getClass().getMethods()) {
                if(method.getName().equals(methodName)) {
                    found = true;
                    if(method.isAnnotationPresent(FromXML.class)) {
                        Class<?> type = method.getParameters()[0].getType();
                        if(type.equals(boolean.class)) {
                            method.invoke(component, Boolean.parseBoolean(value));
                        } else if(type.equals(String.class)) {
                            method.invoke(component, value);
                        } else if(type.equals(int.class)) {
                            method.invoke(component, Integer.parseInt(value));
                        } else if(type.equals(double.class)) {
                            method.invoke(component, Double.parseDouble(value));
                        } else if(type.equals(float.class)) {
                            method.invoke(component, Float.parseFloat(value));
                        } else {
                            throw new RuntimeException("FromXML doesn't support this parameter type: " + method.getParameters()[0].getType().getCanonicalName());
                        }
                        break;
                    }
                }
            }

            if(!found) {
                throw new NoSuchMethodException("Not found method. Maybe it's not annotated as FromXML?");
            }
        } catch(Exception e) {
            printError(component.getObject().getIdentifier(), component.getComponentName());
            Logger.exception("Exception while handling SET component method", e);
        }
    }

    public static void printError(String identifier, String componentName) {
        String component = "";
        if(componentName != null) {
            component = " (component " + componentName + ")";
        }
        Logger.error("Error while deserializing " + identifier + component);
    }

}
