package pl.AWTGameEngine.engine.deserializers;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.Shaders;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.lists.*;
import pl.AWTGameEngine.objects.render.Material;
import pl.AWTGameEngine.objects.render.Sprite;
import pl.AWTGameEngine.objects.render.shaders.Shader;
import pl.AWTGameEngine.objects.transform.Vector4;
import pl.AWTGameEngine.objects.transform.Vector3;

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

    public void handleSetMethod(Object object, String methodName, String value) {
        try {
            boolean found = false;
            for(Method method : object.getClass().getMethods()) {
                if(method.getName().equals(methodName)) {
                    if(method.isAnnotationPresent(FromXML.class)) {
                        found = true;
                        Class<?> type = method.getParameters()[0].getType();
                        boolean handlerFound = false;
                        for(ParameterTypeHandler handler : handlers) {
                            if(handler.equalsTypeClass(type)) {
                                handlerFound = true;
                                handler.invoke(method, object, value);
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
                throw new NoSuchMethodException("Not found method " + methodName + ". Maybe it's not annotated as FromXML?");
            }
        } catch(Exception e) {
            if(object instanceof ObjectComponent component) {
                printError(component.getObject().getIdentifier(), component.getComponentName());
            }
            Logger.exception("Exception while handling SET method", e);
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
        // Boolean
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return boolean.class.equals(type) || Boolean.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, Boolean.parseBoolean(value));
            }
        });
        // String
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return String.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, value);
            }
        });
        // Integer
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return int.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, Integer.parseInt(value));
            }
        });
        // ArrayList<Integer> (IntegerValues)
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return IntegerValues.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, new IntegerValues(value));
            }
        });
        // Long
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return long.class.equals(type) || Long.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, Long.parseLong(value));
            }
        });
        // ArrayList<Long> (LongValues)
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return LongValues.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, new LongValues(value));
            }
        });
        // Double
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return double.class.equals(type) || Double.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, Double.parseDouble(value));
            }
        });
        // ArrayList<Double> (DoubleValues)
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return DoubleValues.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, new DoubleValues(value));
            }
        });
        // Float
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return float.class.equals(type) || Float.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, Float.parseFloat(value));
            }
        });
        // ArrayList<Float> (FloatValues)
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return FloatValues.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, new FloatValues(value));
            }
        });
        // Vector3
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return Vector3.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, new Vector3().deserialize(value));
            }
        });
        // ArrayList<Vector3> (Vector3Values)
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return Vector3Values.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, new Vector3Values(value));
            }
        });
        // Vector4
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return Vector4.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, new Vector4().deserialize(value));
            }
        });
        // ColorObject
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return ColorObject.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, new ColorObject(value));
            }
        });
        // Sprite
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return Sprite.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                method.invoke(object, Dependencies.getResourceManager().getResourceAsSprite(value));
            }
        });
        // Shader
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return Shader.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                if(object instanceof ObjectComponent component) {
                    method.invoke(object, Shaders.of(component.getObject().getScene().getWindow(), Class.forName(value).asSubclass(Shader.class)));
                }
            }
        });
        // Material
        addParameterTypeHandler(new ParameterTypeHandler() {
            @Override
            public boolean equalsTypeClass(Class<?> type) {
                return Material.class.equals(type);
            }

            @Override
            public void invoke(Method method, Object object, String value) throws Exception {
                if(object instanceof ObjectComponent component) {
                    method.invoke(component, component.getObject().getScene().getMaterial(value));
                }
            }
        });
    }

}
