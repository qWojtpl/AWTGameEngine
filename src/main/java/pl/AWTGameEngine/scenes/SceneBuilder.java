package pl.AWTGameEngine.scenes;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.ResourceManager;

//todo
public class SceneBuilder {

    private static final ResourceManager resourceManager;

    static {
        resourceManager = Dependencies.getResourceManager();
    }

    public static void build(String path) {
        Logger.info(path);
    }

}
