package pl.AWTGameEngine.engine;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

import java.lang.reflect.InvocationTargetException;

public class GameObjectDeserializer {

    public static void deserialize(GameObject gameObject, Node data) {
        try {
            if(!getValue(data, "position").equals("0")) {
                gameObject.setPosition(new TransformSet().deserialize(getValue(data, "position")));
            } else {
                gameObject.setX(Integer.parseInt(getValue(data, "x")));
                gameObject.setY(Integer.parseInt(getValue(data, "y")));
                gameObject.setZ(Integer.parseInt(getValue(data, "z")));
            }
            if(!getValue(data, "size").equals("0")) {
                gameObject.setSize(new TransformSet().deserialize(getValue(data, "size")));
            } else {
                gameObject.setSizeX(Integer.parseInt(getValue(data, "sizeX")));
                gameObject.setSizeY(Integer.parseInt(getValue(data, "sizeY")));
                gameObject.setSizeZ(Integer.parseInt(getValue(data, "sizeZ")));
            }
            if(!getValue(data, "rotation").equals("0")) {
                gameObject.setSize(new TransformSet().deserialize(getValue(data, "rotation")));
            } else {
                gameObject.setRotationX(Integer.parseInt(getValue(data, "rotationX")));
                gameObject.setRotationY(Integer.parseInt(getValue(data, "rotationY")));
                gameObject.setRotationZ(Integer.parseInt(getValue(data, "rotationZ")));
            }
            if(getValue(data, "active").equals("0")) {
                gameObject.setActive(true);
            } else {
                gameObject.setActive(Boolean.parseBoolean(getValue(data, "active")));
            }
            for(int i = 0; i < data.getChildNodes().getLength(); i++) {
                Node childNode = data.getChildNodes().item(i);
                if(childNode.getNodeName().equals("object") || childNode.getNodeName().equals("#text")) {
                    continue;
                }
                if(childNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                String className = ((Element) childNode).getTagName();
                String pckg = getValue(childNode, "_package");
                if(!pckg.equals("0")) {
                    className = pckg + "." + className;
                } else {
                    className = "pl.AWTGameEngine.components." + className;
                }
                Class<? extends ObjectComponent> clazz = Class.forName(className)
                        .asSubclass(ObjectComponent.class);
                ObjectComponent o = clazz.getConstructor(GameObject.class).newInstance(gameObject);
                if(childNode.getAttributes() == null) {
                    continue;
                }
                for(int j = 0; j < childNode.getAttributes().getLength(); j++) {
                    String fieldName = childNode.getAttributes().item(j).getNodeName();
                    if(fieldName.equals("_package")) {
                        continue;
                    }
                    String value = childNode.getAttributes().item(j).getNodeValue();
                    String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    if (clazz.getSuperclass().equals(ObjectComponent.class)) {
                        if (clazz.getDeclaredMethod(methodName, String.class).isAnnotationPresent(SerializationSetter.class)) {
                            clazz.getDeclaredMethod(methodName, String.class).invoke(o, value);
                        } else {
                            Logger.log(1, "Tried to invoke " + methodName
                                    + " in serialization (" + className + "), but this method is not annotated as SerializationMethod");
                        }
                    } else {
                        if (clazz.getSuperclass().getDeclaredMethod(methodName, String.class).isAnnotationPresent(SerializationSetter.class)) {
                            clazz.getSuperclass().getDeclaredMethod(methodName, String.class).invoke(o, value);
                        } else {
                            Logger.log(1, "Tried to invoke " + methodName
                                    + " in serialization (" + className + "), but this method is not annotated as SerializationMethod");
                        }
                    }
                }
                gameObject.addComponent(o);
            }
        } catch(NumberFormatException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                InvocationTargetException | ClassNotFoundException | ClassCastException e) {
            Logger.log("Error while deserializing GameObject: " + gameObject.getIdentifier(), e);
        }
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
