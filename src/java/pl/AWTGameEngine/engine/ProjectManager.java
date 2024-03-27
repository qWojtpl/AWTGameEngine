package pl.AWTGameEngine.engine;

import org.w3c.dom.NodeList;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.io.*;
import java.nio.file.Files;
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
        copyResource("defaultTemplate/app.properties", path + "app.properties");
        copyResource("defaultTemplate/main.xml", path + "scenes/main.xml");
        copyResource("defaultTemplate/main.properties", path + "scenes/main.properties");
        copyResource("defaultTemplate/beaver.jpg", path + "sprites/beaver.jpg");
        copyResource("defaultTemplate/error.png", path + "sprites/base/error.jpg");
        copyResource("defaultTemplate/success.png", path + "sprites/base/success.jpg");
        new File(path + "java/").mkdir();
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
/*        NodeList data = window.getSceneLoader().getSceneData("./projects/" + name + "/" +
                AppProperties.getProperty("main", customProperties));
        if(data == null) {
            Logger.log(1, "Cannot attach scene to existing scene.");
            return;
        }
        window.getSceneLoader().attachSceneData(data, parent);*/
        openedProjectName = name;
    }

    private void copyResource(String name, String path) {
        ResourceManager.copyResource(name, path);
    }

    public void compileProject() {
        if(openedProjectName == null) {
            return;
        }
        File jarFile = new File("./AWTGameEngine.jar");
        if(!jarFile.exists()) {
            Logger.log(2, "You can't compile project from non-archive reference.");
            return;
        }
        String path = "./projects/" + openedProjectName + "/";
        File projectDirectory = new File(path);
        File binDirectory = new File(path + "_bin/");
        binDirectory.mkdir();
        new File(path + "_builds/").mkdir();
        Calendar calendar = Calendar.getInstance();
        int index = 0;
        String buildName;
        do {
            index++;
            buildName =
                    calendar.get(Calendar.DAY_OF_MONTH) + "-" +
                    (calendar.get(Calendar.MONTH) + 1) + "-" +
                    calendar.get(Calendar.YEAR) + "-" + index;
        } while(new File(path + "_builds/" + buildName + "/").exists());
        File buildDirectory = new File(path + "_builds/" + buildName + "/");
        buildDirectory.mkdir();
        File outputJar = new File(buildDirectory.getAbsolutePath() + "/output.jar");
        Logger.log(2, "Unpacking archive...\n" +
                executeCommand("cmd.exe", "cd " + binDirectory.getAbsolutePath() + " & jar xf " + jarFile.getAbsolutePath()));
        Logger.log(2, "Creating output archive...\n" +
                executeCommand("cmd.exe", "cd " + binDirectory.getAbsolutePath() + " & jar cmvf META-INF/MANIFEST.MF "
                + outputJar.getAbsolutePath() + " pl"));
        StringBuilder resources = new StringBuilder();
        File[] directories = projectDirectory.listFiles();
        if(directories != null) {
            for(File file : directories) {
                if(!file.getName().startsWith("_")) {
                    resources.append(file.getName());
                    resources.append(" ");
                }
            }
        }
        Logger.log(2, "Copying resources...\n" + executeCommand("cmd.exe", "cd " + projectDirectory.getAbsolutePath() + " & jar uf " + outputJar.getAbsolutePath()
                + " " + resources));
    }

    private String executeCommand(String source, String command) {
        try {
            ProcessBuilder processBuilder;
            if(source.equals("cmd.exe")) {
                processBuilder = new ProcessBuilder(source, "/c", command);
            } else {
                processBuilder = new ProcessBuilder(source, command);
            }
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            readStream(process.getInputStream(), output);
            readStream(process.getErrorStream(), output);
            return output.toString();
        } catch(Exception e) {
            Logger.log("Cannot execute command " + command, e);
        }
        return "";
    }

    private void readStream(InputStream inputStream, StringBuilder output) throws IOException {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
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
