package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SceneStateSaver {

    public static void saveState(String path, Scene scene) {
        Logger.info("Saving scene state...");
        List<GameObject> objects = scene.getGameObjects();
        StringBuilder bobTheBuilder = new StringBuilder();
        addSceneParams(scene, bobTheBuilder);
        for(GameObject object : objects) {
            bobTheBuilder.append(saveObjectState(object));
        }
        bobTheBuilder.append("\n</scene>");
        Logger.info("Saved scene state to: " + (path == null ? "LOGGER" : path));
        if(path == null) {
            Logger.info(bobTheBuilder.toString());
            return;
        }
        try(FileWriter writer = new FileWriter(path, false)) {
            writer.write(bobTheBuilder.toString());
        } catch(IOException e) {
            Logger.exception("Cannot save scene state to file", e);
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
            parameterBuilder.append(" ");
            parameterBuilder.append(saveState.name());
            parameterBuilder.append("=\"");
            try {
                Object result = method.invoke(object);
                String value = String.valueOf(result);
                if(result instanceof TransformSet) {
                    value = ((TransformSet) result).toSimpleString();
                }
                parameterBuilder.append(value.replace("\"", "\\\""));
            } catch (IllegalAccessException | InvocationTargetException e) {
                Logger.exception("Cannot save " + name + "'s " + saveState.name() + " state", e);
            }
            parameterBuilder.append("\"");
        }
        return parameterBuilder.toString();
    }

}
