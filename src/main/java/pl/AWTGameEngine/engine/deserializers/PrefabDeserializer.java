package pl.AWTGameEngine.engine.deserializers;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Prefab;
import pl.AWTGameEngine.scenes.Scene;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import static pl.AWTGameEngine.engine.deserializers.GameObjectDeserializer.getValue;

public class PrefabDeserializer {

    public static void deserialize(String prefabIdentifier, Scene scene, Node node) {
        Prefab prefab = new Prefab(prefabIdentifier);
        deserializePrefabComponents(prefab, node);
        scene.addPrefab(prefab);
    }

    private static void deserializePrefabComponents(Prefab prefab, Node node) {
        String className = "";
        try {
            for(int i = 0; i < node.getChildNodes().getLength(); i++) {
                Node childNode = node.getChildNodes().item(i);
                if(childNode.getNodeName().equals("prefab") || childNode.getNodeName().startsWith("#")) {
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
                Prefab.PrefabComponent prefabComponent = new Prefab.PrefabComponent(prefab, clazz, new HashMap<>());
                deserializeComponentAttributes(prefab, prefabComponent, childNode);
                prefab.addComponent(prefabComponent);
            }
        } catch(ClassNotFoundException e) {
            printError(prefab.getIdentifier(), className);
            Logger.exception("Component not found", e);
        }
    }

    private static void deserializeComponentAttributes(Prefab prefab, Prefab.PrefabComponent component, Node node) {
        String methodName = "";
        try {
            for(int i = 0; i < node.getAttributes().getLength(); i++) {
                String fieldName = node.getAttributes().item(i).getNodeName();
                if(fieldName.equals("_package")) {
                    continue;
                }
                String value = node.getAttributes().item(i).getNodeValue();
                methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                if(component.getComponentClass().getMethod(methodName, String.class).isAnnotationPresent(FromXML.class)) {
                    component.getValues().put(methodName, value);
                } else {
                    printError(prefab.getIdentifier(), component.getComponentClass().getSimpleName());
                    Logger.error("Method " + methodName + " is not annotated as FromXML");
                }
            }
        } catch(NoSuchMethodException e) {
            printError(prefab.getIdentifier(), component.getComponentClass().getSimpleName());
            Logger.exception("Method " + methodName + " not found", e);
        }
    }

    private static void printError(String identifier, String componentName) {
        String component = "";
        if(componentName != null) {
            component = " (component " + componentName + ")";
        }
        Logger.error("Error while deserializing Prefab " + identifier + component);
    }

    public static void injectPrefab(Prefab prefab, GameObject object) {
        try {
            for(Prefab.PrefabComponent prefabComponent : prefab.getComponents()) {
                ObjectComponent objectComponent = prefabComponent.getComponentClass().getConstructor(GameObject.class).newInstance(object);
                for(String methodName : prefabComponent.getValues().keySet()) {
                    Method method = objectComponent.getClass().getMethod(methodName, String.class);
                    if(!method.isAnnotationPresent(FromXML.class)) {
                        continue;
                    }
                    method.invoke(objectComponent, prefabComponent.getValues().get(methodName));
                }
                object.addComponent(objectComponent);
            }
            object.triggerSerializationFinish();
        } catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Logger.exception("Cannot inject prefab " + prefab.getIdentifier() + " to object " + object.getIdentifier(), e);
        }

    }

}
