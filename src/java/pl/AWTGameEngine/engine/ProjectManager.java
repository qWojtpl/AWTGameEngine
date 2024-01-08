package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.windows.Window;

import java.io.File;
import java.util.LinkedHashMap;

public class ProjectManager {

    private final Window window;

    public ProjectManager(Window window) {
        this.window = window;
    }

    public void createProject(String name) {
        String path = "./projects/" + name + "/";
        File projectDir = new File(path);
        if(!projectDir.mkdirs()) {
            Logger.log(1, "Project " + name + " already exists.");
            return;
        }
        String[] resources = new String[] {
                "app.properties",
                "sprites/base/error.png",
                "sprites/base/success.png",
                "scenes/main.scene",
                "scenes/main.properties"
        };
        for(String resource : resources) {
            copyResource(resource, path);
        }
        Logger.log(2, "Created project: " + name);
    }

    public void openProject(String name) {
        File projectDirectory = new File("./projects/" + name + "/");
        if(!projectDirectory.exists()) {
            Logger.log(1, "Cannot open project " + name + ", project doesn't exists.");
            return;
        }
        LinkedHashMap<String, String> data = window.getSceneLoader().getSceneData("./projects/test/scenes/error.scene");
        if(data == null) {
            Logger.log(1, "Cannot attach scene to existing scene with panel.");
            return;
        }
        window.getSceneLoader().attachSceneData(data, window.getCurrentScene().getGameObjectByName("panel"));
    }

    private void copyResource(String name, String path) {
        ResourceManager.copyResource(name, path + name);
    }

    public Window getWindow() {
        return this.window;
    }

}
