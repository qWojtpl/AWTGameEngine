package pl.AWTGameEngine.scenes;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.ResourceManager;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Scanner;

public class SceneBuilder {

    private static final ResourceManager resourceManager;
    private static int addressCounter = 0;

    static {
        resourceManager = Dependencies.getResourceManager();
    }

    public static void build(String path) {
        Logger.info("Starting scene building: " + path);

        StringBuilder fileBuilder = new StringBuilder();
        appendStructure(fileBuilder, getFileName(path));

        try(InputStream stream = resourceManager.getResourceAsStream(path)) {
            SceneLoader sceneLoader = new SceneLoader(null);
            Document document = sceneLoader.getDocument(stream);
            SceneOptions options = sceneLoader.getSceneOptions(document);

            appendOptions(fileBuilder, options);

            NodeList data = sceneLoader.getSceneData(document);

            appendTrailer(fileBuilder);

            Logger.info("Creating file...");

            File directory = new File("./scenebuilder/");
            directory.mkdir();

            File file = new File("./scenebuilder/" + getFileName(path) + ".java");
            boolean b = file.createNewFile();
            if(!b) {
                Logger.warning("Scene file with this name already exists. Do you wish to continue? (Y/n)");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                if(!input.equals("Y")) {
                    throw new RuntimeException("Building aborted.");
                }
            }

            try(FileWriter writer = new FileWriter(file)) {
                writer.write(fileBuilder.toString());
            }

            Logger.info("Building java...");



            Logger.info("Scene " + path + " successfully built and saved in ./scenebuilder/" + getFileName(path) + ".class");

        } catch(Exception e) {
            Logger.exception("Cannot build scene " + path, e);
        }
    }

    private static String getFileName(String path) {
        String[] split = path.split("/");
        return split[split.length - 1].replace(".", "_");
    }

    private static void appendStructure(StringBuilder fileBuilder, String fileName) {
        fileBuilder.append("package pl.AWTGameEngine.scenebuilder;\n\n");
        fileBuilder.append("public class ");
        fileBuilder.append(fileName);
        fileBuilder.append(" {\n\n");
        fileBuilder.append("\tpublic static void load(Object window, Object scene) throws Exception {\n");
    }

    private static void appendOptions(StringBuilder fileBuilder, SceneOptions options) {
        appendMethodBody(fileBuilder, "/* Scene options */");
        appendMethodBody(fileBuilder, createCall("window", "setTitle", "\"" + options.getTitle() + "\""));
        appendMethodBody(fileBuilder, createCall("window", "setFullscreen", String.valueOf(options.isFullscreen())));
        // FPS
        int[] values = new int[]{ options.getUpdateFPS(), options.getRenderFPS(), options.getPhysicsFPS() };
        String[] names = new String[]{ "Update", "Render", "Physics" };
        for(int i = 0; i < values.length; i++) {
            String address = getAddress();
            appendMethodBody(fileBuilder, "Object " + address + " = " + createCall("window", "get" + names[i] + "Loop", "window"));
            appendMethodBody(fileBuilder, createCall(address, "setTargetFPS", String.valueOf(values[i])));
        }
        appendMethodBody(fileBuilder, createCall("window", "setSameSize", String.valueOf(options.isSameSize())));
    }

    private static void appendTrailer(StringBuilder fileBuilder) {
        fileBuilder.append("\t}\n");
        fileBuilder.append("}");
    }

    private static void appendMethodBody(StringBuilder fileBuilder, String m) {
        fileBuilder.append("\t\t");
        fileBuilder.append(m);
        fileBuilder.append(";\n");
    }

    private static String createCall(String object, String field, String value) {
        return object + ".getClass().getMethod(\"" + field + "\").invoke(" + object + ", " + value + ")";
    }

    private static String getAddress() {
        return "x" + Integer.toHexString(addressCounter++);
    }

}
