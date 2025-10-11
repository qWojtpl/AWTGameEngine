package pl.AWTGameEngine.engine.deserializers;

import pl.AWTGameEngine.engine.RenderEngine;
import pl.AWTGameEngine.scenes.Scene;

public class NestedSceneDeserializer {

    public static void deserialize(Scene scene, String sceneSource, String renderEngineStr) {
        RenderEngine renderEngine = RenderEngine.valueOf(renderEngineStr.toUpperCase());
        scene.loadAfterLoad(sceneSource, renderEngine);
    }

}