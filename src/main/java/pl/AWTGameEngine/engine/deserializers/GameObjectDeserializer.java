package pl.AWTGameEngine.engine.deserializers;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import pl.AWTGameEngine.annotations.FromXML;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

import java.lang.reflect.InvocationTargetException;

public class GameObjectDeserializer {

    public static void deserialize(GameObject gameObject, Node data) {
        deserializeObjectAttributes(gameObject, data);
        deserializeObjectComponents(gameObject, data);
        for(ObjectComponent component : gameObject.getEventHandler().getComponents("onSerializationFinish")) {
            component.onSerializationFinish();
        }
    }

    private static void deserializeObjectAttributes(GameObject object, Node node) {
        if(!getValue(node, "position").equals("0")) {
            object.setPosition(new TransformSet().deserialize(getValue(node, "position")));
        } else {
            object.setX(Double.parseDouble(getValue(node, "x")));
            object.setY(Double.parseDouble(getValue(node, "y")));
            object.setZ(Double.parseDouble(getValue(node, "z")));
        }
        if(!getValue(node, "size").equals("0")) {
            object.setSize(new TransformSet().deserialize(getValue(node, "size")));
        } else {
            object.setSizeX(Double.parseDouble(getValue(node, "sizeX")));
            object.setSizeY(Double.parseDouble(getValue(node, "sizeY")));
            object.setSizeZ(Double.parseDouble(getValue(node, "sizeZ")));
        }
        if(!getValue(node, "rotation").equals("0")) {
            object.setSize(new TransformSet().deserialize(getValue(node, "rotation")));
        } else {
            double x = Double.parseDouble(getValue(node, "rotationX"));
            double y = Double.parseDouble(getValue(node, "rotationY"));
            double z = Double.parseDouble(getValue(node, "rotationZ"));
            object.setRotation(new TransformSet(x, y, z));
        }
        if(getValue(node, "active").equals("0")) {
            object.setActive(true);
        } else {
            object.setActive(Boolean.parseBoolean(getValue(node, "active")));
        }
    }

    private static void deserializeObjectComponents(GameObject object, Node node) {
        String className = "";
        try {
            for(int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node childNode = node.getChildNodes().item(i);
                if(childNode.getNodeName().equals("object") || childNode.getNodeName().startsWith("#")) {
                    continue;
                }
                if(childNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                className = ((Element) childNode).getTagName().replace(".", "$");
                String pckg = getValue(childNode, "_package");
                if(!pckg.equals("0")) {
                    className = pckg + "." + className;
                } else {
                    className = "pl.AWTGameEngine.components." + className;
                }
                Class<? extends ObjectComponent> clazz = Class.forName(className)
                        .asSubclass(ObjectComponent.class);
                ObjectComponent o = clazz.getConstructor(GameObject.class).newInstance(object);
                if(childNode.getAttributes() == null) {
                    continue;
                }
                deserializeComponentAttributes(o, clazz, className, childNode);
                object.addComponent(o);
            }
        } catch(ClassNotFoundException e) {
            printError(object.getIdentifier(), className);
            Logger.exception("Component not found", e);
        } catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            printError(object.getIdentifier(), className);
            Logger.exception("Cannot find or use constructor with GameObject, are you sure you didn't touch anything?", e);
        }
    }

    private static void deserializeComponentAttributes(ObjectComponent component, Class<?> componentClass, String componentName, Node node) {
        String methodName = "";
        try {
            for(int j = 0; j < node.getAttributes().getLength(); j++) {
                String fieldName = node.getAttributes().item(j).getNodeName();
                if(fieldName.equals("_package")) {
                    continue;
                }
                String value = node.getAttributes().item(j).getNodeValue();
                methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                if (componentClass.getMethod(methodName, String.class).isAnnotationPresent(FromXML.class)) {
                    componentClass.getMethod(methodName, String.class).invoke(component, value);
                } else {
                    printError(component.getObject().getIdentifier(), componentName);
                    Logger.error("Method " + methodName + " is not annotated as FromXML");
                }
            }
        } catch(NoSuchMethodException e) {
            printError(component.getObject().getIdentifier(), componentName);
            Logger.exception("Method " + methodName + " not found", e);
        } catch(IllegalAccessException e) {
            printError(component.getObject().getIdentifier(), componentName);
            Logger.exception("Can't access method " + methodName, e);
        } catch(InvocationTargetException e) {
            printError(component.getObject().getIdentifier(), componentName);
            Logger.exception("Method " + methodName + " invocation exception (probable type mismatch)", e);
        }
    }

    private static void printError(String identifier, String componentName) {
        String component = "";
        if(componentName != null) {
            component = " (component " + componentName + ")";
        }
        Logger.error("Error while deserializing GameObject " + identifier + component);
    }

    private static String getValue(Node node, String name) {
        if(node.getAttributes() == null) {
            return "0";
        }
        Node namedItem = node.getAttributes().getNamedItem(name);
        if(namedItem == null) {
            return "0";
        }
        return namedItem.getNodeValue();
    }

}
