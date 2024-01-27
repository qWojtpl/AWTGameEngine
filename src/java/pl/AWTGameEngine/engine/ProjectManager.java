package pl.AWTGameEngine.engine;

import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.io.File;
import java.util.*;

public class ProjectManager {

    private final Window window;
    private String openedProjectName;

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

    public void openProject(GameObject parent, String name) {
        File projectDirectory = new File("./projects/" + name + "/");
        if(!projectDirectory.exists()) {
            Logger.log(1, "Cannot open project " + name + ", project doesn't exists.");
            return;
        }
        Properties customProperties = AppProperties.getCustomProperties("./projects/" + name + "/app.properties");
        if(customProperties == null) {
            Logger.log(1, "Cannot open project " + name + ", app.properties doesn't exists.");
            return;
        }
        LinkedHashMap<String, String> data = window.getSceneLoader().getSceneData("./projects/" + name + "/" +
                AppProperties.getProperty("main", customProperties));
        if(data == null) {
            Logger.log(1, "Cannot attach scene to existing scene with panel.");
            return;
        }
        window.getSceneLoader().attachSceneData(data, parent);
        openedProjectName = name;
    }

    private void copyResource(String name, String path) {
        ResourceManager.copyResource(name, path + name);
    }

    public Window getWindow() {
        return this.window;
    }

    public List<String> getProjectNames() {
        File projectsDirectory = new File("./projects/");
        if(!projectsDirectory.exists()) {
            return new ArrayList<>();
        }
        File[] projects = projectsDirectory.listFiles(File::isDirectory);
        if(projects == null) {
            return new ArrayList<>();
        }
        List<String> projectNames = new ArrayList<>();
        for(File directory : projects) {
            projectNames.add(directory.getName());
        }
        return projectNames;
    }

    public List<File> getProjectFiles(String subDirectory) {
        if(openedProjectName == null) {
            return new ArrayList<>();
        }
        File projectDirectory = new File("./projects/" + openedProjectName +
                (subDirectory == null ? "/" : "/" + subDirectory + "/"));
        if(!projectDirectory.exists()) {
            return new ArrayList<>();
        }
        File[] files = projectDirectory.listFiles();
        if(files == null) {
            return new ArrayList<>();
        }
        List<File> fileList = Arrays.asList(files);
        Collections.reverse(fileList);
        return fileList;
    }

    public String getOpenedProjectName() {
        return this.openedProjectName;
    }

}
