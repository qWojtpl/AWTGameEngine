package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.enums.RenderEngine;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Prefab;
import pl.AWTGameEngine.objects.TransformSet;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SceneStateSaver {

    public static void saveState(String path, Scene scene, boolean build) {
        Logger.info("Saving scene state...");
        List<GameObject> objects = scene.getGameObjects();
        StringBuilder bobTheBuilder = new StringBuilder("<!-- Auto-generated using SceneStateSaver at " + System.currentTimeMillis() / 1000L + " -->\n");
        addSceneParams(scene, bobTheBuilder);
        addNestedScenes(scene, bobTheBuilder);
        addPrefabs(scene, bobTheBuilder);
        for(GameObject object : objects) {
            bobTheBuilder.append(saveObjectState(object));
        }
        addStyles(scene, bobTheBuilder);
        bobTheBuilder.append("\n</scene>");
        Logger.info("Saved scene state to: " + (path == null ? "LOGGER" : path));
        if(path == null) {
            Logger.info(bobTheBuilder.toString());
            return;
        }
        try(FileWriter writer = Dependencies.getResourceManager().getWriter(path, false)) {
            writer.write(bobTheBuilder.toString());
        } catch(IOException e) {
            Logger.exception("Cannot save scene state to file", e);
        }
        if(build) {
            SceneBuilder.build(path, true);
        }
    }

    private static void addSceneParams(Scene scene, StringBuilder builder) {
        builder.append("<scene title=\"");
        builder.append(scene.getWindow().getTitle());
        builder.append("\" renderFPS=\"");
        builder.append((int) scene.getWindow().getRenderLoop().getTargetFps());
        builder.append("\" updateFPS=\"");
        builder.append((int) scene.getWindow().getUpdateLoop().getTargetFps());
        builder.append("\" physicsFPS=\"");
        builder.append((int) scene.getWindow().getPhysicsLoop().getTargetFps());
        builder.append("\" sameSize=\"");
        builder.append(scene.getWindow().isSameSize());
        builder.append("\" fullscreen=\"");
        builder.append(scene.getWindow().isFullScreen());
        builder.append("\">");
    }

    private static void addNestedScenes(Scene scene, StringBuilder builder) {
        HashMap<String, RenderEngine> loadAfterLoad = scene.getLoadAfterLoad();
        for(String path : loadAfterLoad.keySet()) {
            builder.append("\n\t<scene source=\"");
            builder.append(path);
            builder.append("\" renderEngine=\"");
            builder.append(loadAfterLoad.get(path));
            builder.append("\" />");
        }
    }

    private static void addStyles(Scene scene, StringBuilder builder) {
        //todo: styles from file
        if(scene.getCustomStyles().isEmpty()) {
            return;
        }
        builder.append("\n\t<styles>\n").append(scene.getCustomStyles()).append("\n\t</styles>");
    }

    private static void addPrefabs(Scene scene, StringBuilder builder) {
        Set<String> externalPaths = new HashSet<>();
        for(Prefab prefab : scene.getPrefabs()) {
            if(prefab.getExternalPrefabPath() == null) {
                builder.append("\n\t<prefab id=\"").append(prefab.getIdentifier()).append("\">");
                for(Prefab.PrefabComponent prefabComponent : prefab.getComponents()) {
                    builder.append("\n\t\t<");
                    builder.append(prefabComponent.getComponentClass().getCanonicalName().replace(prefabComponent.getComponentClass().getPackageName() + ".", ""));
                    for(String methodName : prefabComponent.getValues().keySet()) {
                        String fieldName = methodName.replaceFirst("set", "");
                        fieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                        builder.append(" ").append(fieldName).append("=\"").append(prefabComponent.getValues().get(methodName)).append("\"");
                    }
                    if(!prefabComponent.getComponentClass().getPackageName().equals("pl.AWTGameEngine.components")) {
                        builder.append(" _package=\"");
                        builder.append(prefabComponent.getComponentClass().getPackageName());
                        builder.append("\"");
                    }
                    builder.append(" />");
                }
                builder.append("\n\t</prefab>");
            } else {
                externalPaths.add(prefab.getExternalPrefabPath());
            }
        }
        for(String externalPath : externalPaths) {
            builder.append("\n\t<prefabs source=\"").append(externalPath).append("\" />");
        }
    }

    private static String saveObjectState(GameObject object) {
        StringBuilder objectBuilder = new StringBuilder();
        objectBuilder.append("\n\t<object");
        objectBuilder.append(getParameters(object, object.getIdentifier()));
        objectBuilder.append(">\n");
        List<ObjectComponent> components = object.getComponents();
        for(ObjectComponent component : components) {
            objectBuilder.append("\t\t");
            objectBuilder.append("<");
            objectBuilder.append(component.getClass().getCanonicalName().replace(component.getClass().getPackageName() + ".", ""));
            objectBuilder.append(getParameters(component, component.getComponentName()));
            if(!component.getClass().getPackageName().equals("pl.AWTGameEngine.components")) {
                objectBuilder.append(" _package=\"");
                objectBuilder.append(component.getClass().getPackageName());
                objectBuilder.append("\"");
            }
            objectBuilder.append(" />\n");
        }
        objectBuilder.append("\t</object>");
        return objectBuilder.toString();
    }

    private static String getParameters(Object object, String name) {
        StringBuilder parameterBuilder = new StringBuilder();
        for(Method method : object.getClass().getMethods()) {
            if(!method.isAnnotationPresent(SaveState.class)) {
                continue;
            }
            SaveState saveState = method.getAnnotation(SaveState.class);
            try {
                Object result = method.invoke(object);
                String value = String.valueOf(result);
                if(result instanceof TransformSet) {
                    value = ((TransformSet) result).toSimpleString();
                    if(((TransformSet) result).isEmpty()) {
                        continue;
                    }
                }
                parameterBuilder.append(" ");
                parameterBuilder.append(saveState.name());
                parameterBuilder.append("=\"");
                parameterBuilder.append(value.replace("\"", "\\\""));
            } catch (IllegalAccessException | InvocationTargetException e) {
                Logger.exception("Cannot save " + name + "'s " + saveState.name() + " state", e);
            }
            parameterBuilder.append("\"");
        }
        return parameterBuilder.toString();
    }

}
