package pl.AWTGameEngine.scenes;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.ResourceManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SceneBuilder {

    private static final ResourceManager resourceManager;
    private static long addressCounter = 0;

    static {
        resourceManager = Dependencies.getResourceManager();
    }

    public static void build(String path, boolean force) {
        Logger.info("Starting scene building: " + path);

        StringBuilder fileBuilder = new StringBuilder();
        appendStructure(fileBuilder, getFileName(path));

        try(InputStream stream = resourceManager.getResourceAsStream(path)) {
            SceneLoader sceneLoader = new SceneLoader(null);
            Document document = sceneLoader.getDocument(stream);
            SceneOptions options = sceneLoader.getSceneOptions(document);

            appendOptions(fileBuilder, options);
            StringBuilder methodBuilder = new StringBuilder();
            appendChildren(fileBuilder, methodBuilder, sceneLoader.getSceneData(document));
            fileBuilder.append("\t}\n");
            fileBuilder.append(methodBuilder);
            fileBuilder.append("}");

            Logger.info("Creating file...");

            File directory = new File("./scenebuilder/");
            directory.mkdir();

            File file = new File("./scenebuilder/" + getFileName(path) + ".java");
            boolean b = file.createNewFile();
            if(!b && !force) {
                Logger.warning("Scene file with this name already exists. Do you wish to continue? (Y/n)");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                if(!input.equalsIgnoreCase("y")) {
                    throw new RuntimeException("Building aborted.");
                }
            }

            try(FileWriter writer = new FileWriter(file)) {
                writer.write(fileBuilder.toString());
            }

            Logger.info("Building java...");

            //todo: Linux

            String compiler = System.getProperty("java.home") + File.separator + "bin" + File.separator + "javac.exe";

            Process process = Runtime.getRuntime().exec("cmd /c " + compiler + " " + file.getAbsolutePath());

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getErrorStream()));

            boolean errors = false;
            String message = null;
            while ((message = stdError.readLine()) != null) {
                Logger.error(message);
                errors = true;
            }

            process.waitFor();

            if(errors) {
                throw new RuntimeException("Java build ended with errors.");
            } else {
                Logger.info("Scene " + path + " successfully built and saved in ./scenebuilder/" + getFileName(path) + ".class");
            }

        } catch(Exception e) {
            Logger.exception("Cannot build scene " + path, e);
        }
    }

    private static String getFileName(String path) {
        String[] split = path.split("[\\/\\\\]");
        return split[split.length - 1].replace(".", "_");
    }

    private static void appendStructure(StringBuilder fileBuilder, String fileName) {
        fileBuilder.append("/* Auto-generated using SceneBuilder at ");
        fileBuilder.append(System.currentTimeMillis() / 1000L);
        fileBuilder.append(" */\n");
        fileBuilder.append("import java.util.Arrays;\n\n");
        fileBuilder.append("public class ");
        fileBuilder.append(fileName);
        fileBuilder.append(" {\n\n");
        fileBuilder.append("\tpublic static void load(Object window, Object scene) throws Exception {\n");
    }

    private static void appendOptions(StringBuilder fileBuilder, SceneOptions options) {
        appendMethodBody(fileBuilder, "/* Scene options */");
        appendMethodBody(fileBuilder, createCall("window", "setTitle", "String.class", "\"" + options.getTitle() + "\""));
        appendMethodBody(fileBuilder, createCall("window", "setFullScreen", "boolean.class", String.valueOf(options.isFullscreen())));
        // FPS
        double[] values = new double[]{ options.getUpdateFPS(), options.getRenderFPS(), options.getPhysicsFPS() };
        String[] names = new String[]{ "Update", "Render", "Physics" };
        for(int i = 0; i < values.length; i++) {
            String address = getAddress();
            appendMethodBody(fileBuilder, "Object " + address + " = " + createCall("window", "get" + names[i] + "Loop", null));
            appendMethodBody(fileBuilder, createCall(address, "setTargetFps", "double.class", String.valueOf(values[i])));
        }
        appendMethodBody(fileBuilder, createCall("window", "setSameSize", "boolean.class", String.valueOf(options.isSameSize())));
    }

    private static void appendChildren(StringBuilder fileBuilder, StringBuilder methodBuilder, NodeList sceneData) {
        appendMethodBody(fileBuilder, "/* Objects */");
        appendMethodBody(fileBuilder, "java.lang.reflect.Method createGameObjectMethod = scene.getClass().getMethod(\"createGameObject\", String.class)");
        for(int i = 0; i < sceneData.getLength(); i++) {
            if(sceneData.item(i).getNodeName().startsWith("#")) { // #comment or #text
                continue;
            }
            initNode(fileBuilder, methodBuilder, sceneData.item(i));
        }
    }

    private static void initNode(StringBuilder fileBuilder, StringBuilder methodBuilder, Node node) {
        String nodeName = node.getNodeName().toLowerCase();
        switch(nodeName) {
            case "object" -> initObjectNode(fileBuilder, methodBuilder, node);
            case "styles" -> initStyleNode(fileBuilder, methodBuilder, node);
            case "scene" -> initNestedSceneNode(fileBuilder, methodBuilder, node);
            default -> Logger.warning("Unrecognized node name: " + nodeName);
        }
    }

    private static void initObjectNode(StringBuilder fileBuilder, StringBuilder methodBuilder, Node node) {
        String identifier;
        try {
            identifier = node.getAttributes().getNamedItem("id").getNodeValue();
        } catch (Exception e) {
            Logger.exception("Object doesn't have an identifier.", e);
            return;
        }
        String address = getAddress();
        appendMethodBody(fileBuilder, address + "(scene, createGameObjectMethod)");
        methodBuilder.append("\tprivate static void ");
        methodBuilder.append(address);
        methodBuilder.append("(Object scene, java.lang.reflect.Method sceneMethod) throws Exception {\n");
        appendMethodBody(methodBuilder, "Object " + address + " = sceneMethod.invoke(scene, \"" + identifier + "\")");
        //
        String[] position;
        if(getValue(node, "position").equals("0")) {
            position = new String[3];
            position[0] = getValue(node, "x");
            position[1] = getValue(node, "y");
            position[2] = getValue(node, "z");
        } else {
            position = getValue(node, "position").split(",");
        }
        appendMethodBody(methodBuilder, createCall(address, "setX", "double.class", position[0]));
        appendMethodBody(methodBuilder, createCall(address, "setY", "double.class", position[1]));
        appendMethodBody(methodBuilder, createCall(address, "setZ", "double.class", position[2]));
        //
        String[] size;
        if(getValue(node, "size").equals("0")) {
            size = new String[3];
            size[0] = getValue(node, "sizeX");
            size[1] = getValue(node, "sizeY");
            size[2] = getValue(node, "sizeZ");
        } else {
            size = getValue(node, "size").split(",");
        }
        appendMethodBody(methodBuilder, createCall(address, "setSizeX", "double.class", size[0]));
        appendMethodBody(methodBuilder, createCall(address, "setSizeY", "double.class", size[1]));
        appendMethodBody(methodBuilder, createCall(address, "setSizeZ", "double.class", size[2]));
        //
        for(int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node childNode = node.getChildNodes().item(i);
            if(childNode.getNodeName().startsWith("#")) { // #comment or #text
                continue;
            }
            String componentAddress = getAddress();
            String pckg = "pl.AWTGameEngine.components";
            if(childNode.getAttributes().getNamedItem("_package") != null) {
                pckg = childNode.getAttributes().getNamedItem("_package").getNodeValue();
            }
            pckg = pckg + "." + childNode.getNodeName().replace(".", "$");
            appendMethodBody(methodBuilder, "Object " + componentAddress + " = Class.forName(\"" + pckg + "\").getConstructors()[0].newInstance(" + address + ")");

            for(int j = 0; j < childNode.getAttributes().getLength(); j++) {
                Node attribute = childNode.getAttributes().item(j);
                if(attribute.getNodeName().equals("_package")) {
                    continue;
                }
                String[] split = attribute.getNodeName().split("");
                split[0] = split[0].toUpperCase();
                String attributeName = String.join("", split);
                appendMethodBody(methodBuilder, createCall(componentAddress, "set" + attributeName, "String.class", "\"" + attribute.getNodeValue() + "\""));
            }

            appendMethodBody(methodBuilder, createCall(address, "addComponent", componentAddress));
        }
        appendMethodBody(methodBuilder, createCall(address, "triggerSerializationFinish"));
        methodBuilder.append("\t}\n\n");
    }

    private static void initStyleNode(StringBuilder fileBuilder, StringBuilder methodBuilder, Node node) {
        //todo: styles from file
        appendMethodBody(fileBuilder, createCall("scene", "setCustomStyles", "String.class", "\"" + node.getTextContent().replace("\n", "\\n") + "\""));
    }

    private static void initNestedSceneNode(StringBuilder fileBuilder, StringBuilder methodBuilder, Node node) {
        appendMethodBody(fileBuilder, createCall("scene", "loadAfterLoad", "String.class, String.class", "\"" + getValue(node, "source") + "\", \"" + getValue(node, "renderEngine") + "\""));
    }

    private static String getValue(Node node, String name) {
        if(node.getAttributes() == null) {
            return "0";
        }
        Node namedItem = node.getAttributes().getNamedItem(name);
        if(namedItem == null) {
            return "0";
        }
        return namedItem.getNodeValue();
    }

    private static void appendMethodBody(StringBuilder fileBuilder, String m) {
        fileBuilder.append("\t\t");
        fileBuilder.append(m);
        fileBuilder.append(";\n");
    }

    /**
     * Use this method to create call to method when method doesn't have any parameters
     * @param object Object address to invoke the method on
     * @param field  Method to invoke name
     * @return       Created call
     */
    private static String createCall(String object, String field) {
        return object + ".getClass().getMethod(\"" + field + "\").invoke(" + object + ")";
    }

    /**
     * Use this method to create call to method when the parameter is
     * a reference to object with custom class, e.g. ObjectComponent
     * @param object Object address to invoke the method on
     * @param field  Method to invoke name
     * @param value  Address of object with custom class
     * @return       Created call
     */
    private static String createCall(String object, String field, String value) {
        return "Arrays.stream(" + object + ".getClass().getMethods()).filter(method -> method.getName().equals(" + "\"" + field + "\"" + ")).findFirst().orElseThrow(() -> new NoSuchMethodException()).invoke(" + object + (value != null ? ", " + value : "") + ")";
    }

    /**
     * Use this method to create call to method when the parameter can be
     * written as a string. Remember, if the field type is a String.class, you have to use
     * additional quotation marks inside the value string, as shown in the example.
     * <pre>
     *     createCall("x14", "setSizeX", "double.class", "30");
     *     createCall("x14", "multipleParameters", "double.class, double.class", "30.0, 25.5");
     *     createCall("x14", "setIdentifier", "String.class", "\"myIdentifier\"");
     * </pre>
     * @param object    Object address to invoke the method on
     * @param field     Method to invoke name
     * @param fieldType Type of field, e.g. "double.class"
     * @param value     Method parameter(s)
     * @return          Created call
     */
    private static String createCall(String object, String field, String fieldType, String value) {
        return object + ".getClass().getMethod(\"" + field + "\", " + fieldType + ").invoke(" + object + (value != null ? ", " + value : "") + ")";
    }

    private static String getAddress() {
        return "x" + Long.toHexString(addressCounter++);
    }

    public static boolean isSceneBuilder(String[] args) {
        boolean sceneBuilder = false;
        boolean sceneBuilderMode = false;
        boolean force = false;
        List<String> buildArgs = new ArrayList<>();
        for(String arg : args) {
            if(arg.startsWith("-")) {
                if(arg.equalsIgnoreCase("--build")) {
                    sceneBuilder = true;
                    if(!sceneBuilderMode) {
                        sceneBuilderMode = true;
                        Logger.info("Running in SceneBuilder mode!");
                        Logger.warning("Please note: don't build scene every time while debugging/development. " +
                                "Using SceneBuilder is intended to use it before release to provide faster loading.");
                    }
                    continue;
                } else if(arg.equalsIgnoreCase("--force")) {
                    force = true;
                    sceneBuilder = false;
                    continue;
                }
                sceneBuilder = false;
                break;
            } else if(sceneBuilder) {
                buildArgs.add(arg);
            }
        }
        if(!buildArgs.isEmpty()) {
            Logger.info("Scenes to build today: " + String.join(", ", buildArgs));
            for(String buildArg : buildArgs) {
                SceneBuilder.build(buildArg, force);
            }
            Logger.info("SceneBuilder: Done.");
        }
        return sceneBuilderMode;
    }

}
