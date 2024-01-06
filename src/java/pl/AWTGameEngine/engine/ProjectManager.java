package pl.AWTGameEngine.engine;

import java.io.File;

public abstract class ProjectManager {
    public static void createProject(String name) {
        createProjectDirectory();
        File projectDir = new File("./projects/" + name + "/");
        if(!projectDir.mkdir()) {
            Logger.log(1, "Project " + name + " already exists.");
            return;
        }
        ResourceManager.copyResource("app.properties", "./projects/" + name + "/app.properties");
        ResourceManager.copyResource("sprites/beaver.jpg", "./projects/" + name + "/beaver.jpg");
        Logger.log(2, "Created project: " + name);
    }

    public static void createProjectDirectory() {
        File directory = new File("./projects/");
        if(!directory.exists()) {
            if(!directory.mkdir()) {
                Logger.log(1, "Cannot create projects directory!");
            }
        }
    }

}
