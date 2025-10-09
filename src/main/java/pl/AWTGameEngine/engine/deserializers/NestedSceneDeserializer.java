package pl.AWTGameEngine.engine.deserializers;

import pl.AWTGameEngine.engine.RenderEngine;
import pl.AWTGameEngine.scenes.Scene;
import pl.AWTGameEngine.scenes.SceneLoader;

public class NestedSceneDeserializer {

    public static void deserialize(Scene scene, String sceneSource, String renderEngineStr) {
        SceneLoader sceneLoader = scene.getWindow().getSceneLoader();
        RenderEngine renderEngine = RenderEngine.valueOf(renderEngineStr.toUpperCase());
        sceneLoader.loadSceneFile(sceneSource, renderEngine);
    }

}
