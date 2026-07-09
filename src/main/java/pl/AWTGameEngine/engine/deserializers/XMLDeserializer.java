package pl.AWTGameEngine.engine.deserializers;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.transform.QuaternionTransformSet;
import pl.AWTGameEngine.objects.transform.TransformSet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static pl.AWTGameEngine.engine.deserializers.GameObjectDeserializer.getValue;

public class XMLDeserializer {

    private static XMLDeserializer instance;
    private final List<ParameterTypeHandler> handlers = new ArrayList<>();

    XMLDeserializer() {
        addDefaultHandlers();
    }

    public static XMLDeserializer getInstance() {
        if(instance == null) {
            instance = new XMLDeserializer();
        }
        return instance;
    }

    public void addParameterTypeHandler(ParameterTypeHandler handler) {
        handlers.add(handler);
    }

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

    public void handleSetMethod(ObjectComponent component, String methodName, String value) {
        try {
            boolean found = false;
            for(Method method : component.getClass().getMethods()) {
                if(method.getName().equals(methodName)) {
                    found = true;
                    if(method.isAnnotationPresent(FromXML.class)) {
                        Class<?> type = method.getParameters()[0].getType();
                        boolean handlerFound = false;
                        for(ParameterTypeHandler handler : handlers) {
                            if(handler.equalsTypeClass(type)) {
                                handlerFound = true;
                                handler.invoke(method, component, value);
                                break;
                            }
                        }
                        if(!handlerFound) {
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

    private void addDefaultHandlers() {
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return boolean.class.equals(type) || Boolean.class.equals(type);
            }

            @Override
            public void invoke(Method method, ObjectComponent component, String value) throws Exception {
                method.invoke(component, Boolean.parseBoolean(value));
            }
        });
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return String.class.equals(type);
            }

            @Override
            public void invoke(Method method, ObjectComponent component, String value) throws Exception {
                method.invoke(component, value);
            }
        });
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return int.class.equals(type);
            }

            @Override
            public void invoke(Method method, ObjectComponent component, String value) throws Exception {
                method.invoke(component, Integer.parseInt(value));
            }
        });
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return long.class.equals(type) || Long.class.equals(type);
            }

            @Override
            public void invoke(Method method, ObjectComponent component, String value) throws Exception {
                method.invoke(component, Long.parseLong(value));
            }
        });
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return double.class.equals(type) || Double.class.equals(type);
            }

            @Override
            public void invoke(Method method, ObjectComponent component, String value) throws Exception {
                method.invoke(component, Double.parseDouble(value));
            }
        });
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return float.class.equals(type) || Float.class.equals(type);
            }

            @Override
            public void invoke(Method method, ObjectComponent component, String value) throws Exception {
                method.invoke(component, Float.parseFloat(value));
            }
        });
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return TransformSet.class.equals(type);
            }

            @Override
            public void invoke(Method method, ObjectComponent component, String value) throws Exception {
                method.invoke(component, new TransformSet().deserialize(value));
            }
        });
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return QuaternionTransformSet.class.equals(type);
            }

            @Override
            public void invoke(Method method, ObjectComponent component, String value) throws Exception {
                method.invoke(component, new QuaternionTransformSet().deserialize(value));
            }
        });
    }

}
