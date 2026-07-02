package pl.AWTGameEngine.engine.deserializers;

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
import java.util.List;

import static pl.AWTGameEngine.engine.deserializers.XMLDeserializer.printError;

public class PrefabDeserializer {

    public static void deserialize(String prefabIdentifier, Scene scene, Node node, String externalPrefabPath) {
        Prefab prefab = new Prefab(prefabIdentifier, externalPrefabPath);
        deserializePrefabComponents(prefab, scene.getOriginalOptions().getPackages(), node);
        scene.addPrefab(prefab);
    }

    private static void deserializePrefabComponents(Prefab prefab, List<String> packages, Node node) {
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
                Class<? extends ObjectComponent> clazz = XMLDeserializer.getClassFromTag(childNode, packages);
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

                boolean found = false;
                for(Method method : component.getComponentClass().getMethods()) {
                    if(method.getName().equals(methodName)) {
                        if(method.isAnnotationPresent(FromXML.class)) {
                            component.getValues().put(methodName, value);
                            found = true;
                            break;
                        }
                    }
                }

                if(!found) {
                    throw new NoSuchMethodException();
                }
            }
        } catch(NoSuchMethodException e) {
            printError(prefab.getIdentifier(), component.getComponentClass().getSimpleName());
            Logger.exception("Method " + methodName + " not found", e);
        }
    }

    public static void injectPrefab(Prefab prefab, GameObject object, boolean triggerSerializationFinish) {
        try {
            for(Prefab.PrefabComponent prefabComponent : prefab.getComponents()) {
                ObjectComponent objectComponent = prefabComponent.getComponentClass().getConstructor(GameObject.class).newInstance(object);
                for(String methodName : prefabComponent.getValues().keySet()) {
                    XMLDeserializer.getInstance().handleSetMethod(objectComponent, methodName, prefabComponent.getValues().get(methodName));
                }
                object.addComponent(objectComponent);
            }
            if(triggerSerializationFinish) {
                object.triggerSerializationFinish();
            }
        } catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            Logger.exception("Cannot inject prefab " + prefab.getIdentifier() + " to object " + object.getIdentifier(), e);
        }

    }

}
