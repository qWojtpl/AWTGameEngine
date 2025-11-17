package pl.AWTGameEngine.scenes;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.RenderEngine;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.engine.deserializers.*;
import pl.AWTGameEngine.engine.panels.*;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

public class SceneLoader {

    private final Window window;

    public SceneLoader(Window window) {
        this.window = window;
    }

    public void loadSceneFile(String scenePath) {
        loadSceneFile(
                scenePath,
                RenderEngine.valueOf(Dependencies.getAppProperties().getProperty("renderEngine").toUpperCase()),
                false);
    }

    public void loadSceneFile(String scenePath, RenderEngine renderEngine, boolean nestedScene) {
        Logger.info("Loading scene: " + scenePath);
        if(scenePath.endsWith(".class")) {
            loadSceneBinary(scenePath, renderEngine, nestedScene);
            return;
        }
        Scene newScene;
        ResourceManager resourceManager = Dependencies.getResourceManager();
        AppProperties appProperties = Dependencies.getAppProperties();
        try(InputStream sceneStream = resourceManager.getResourceAsStream(scenePath)) {
            Document document = getDocument(sceneStream);
            SceneOptions sceneOptions = getSceneOptions(document);
            if(!nestedScene) {
                String title;
                boolean sameSize;
                if(sceneOptions != null) {
                    title = sceneOptions.getTitle();
                    window.getUpdateLoop().setTargetFps(sceneOptions.getUpdateFPS());
                    window.getRenderLoop().setTargetFps(sceneOptions.getRenderFPS());
                    window.getPhysicsLoop().setTargetFps(sceneOptions.getPhysicsFPS());
                    if(sceneOptions.isFullscreen()) {
                        window.setFullScreen(true);
                    }
                    sameSize = sceneOptions.isSameSize();
                } else {
                    title = appProperties.getProperty("title");
                    sameSize = appProperties.getPropertyAsBoolean("sameSize");
                    window.setFullScreen(appProperties.getPropertyAsBoolean("fullscreen"));
                }
                window.setTitle(title);
                window.setSameSize(sameSize);
            }
            newScene = new Scene(scenePath, window, renderEngine);
            newScene.setPanel(createPanel(newScene, renderEngine));
            window.addScene(newScene);
            if(!nestedScene) {
                window.setCurrentScene(newScene);
            }
            NodeList data = getSceneData(document);
            if(data == null) {
                throw new RuntimeException("No scene data found.");
            }
            attachSceneData(newScene, data);
        } catch(Exception e) {
            Logger.exception("Cannot load scene " + scenePath, e);
            return;
        }
        Logger.info("Scene " + scenePath + " loaded.");
        newScene.triggerAfterLoad();
    }

    public void loadSceneBinary(String scenePath, RenderEngine renderEngine, boolean nestedScene) {
        Scene newScene = new Scene(scenePath, window, renderEngine);
        newScene.setPanel(createPanel(newScene, renderEngine));
        window.addScene(newScene);
        if(!nestedScene) {
            window.setCurrentScene(newScene);
        }
        try {
            String[] split = scenePath.split("[\\/\\\\]");
            String className = split[split.length - 1].replace(".class", "");

            URL url = new File(scenePath.replace(className + ".class", "")).toURI().toURL();
            URL[] urls = new URL[]{url};

            try(URLClassLoader cl = new URLClassLoader(urls)) {

                Class<?> cls = cl.loadClass(className);
                Object object = cls.getConstructor().newInstance();
                object.getClass().getMethod("load", Object.class, Object.class).invoke(object, window, newScene);
            }

        } catch (InvocationTargetException e) {
            Logger.error("Cannot load scene binary " + scenePath + ":\n\t" + e.getCause());
        } catch (Exception e) {
            Logger.exception("Cannot load scene binary " + scenePath, e);
        }
    }

    public PanelObject createPanel(Scene scene, RenderEngine renderEngine) {
        PanelObject panel = null;
        int index = scene.getWindow().getScenes().size();
        if (RenderEngine.DEFAULT.equals(renderEngine)) {
            panel = new DefaultPanel(scene);
            window.getLayeredPane().add((DefaultPanel) panel, Integer.valueOf(index));
        } else if (RenderEngine.WEB.equals(renderEngine)) {
            panel = new WebPanel(scene);
            window.getLayeredPane().add((WebPanel) panel, Integer.valueOf(index));
        } else if (RenderEngine.FX3D.equals(renderEngine)) {
            panel = new PanelFX(scene, scene.getWindow().getBaseWidth(), scene.getWindow().getBaseHeight());
            window.getLayeredPane().add((PanelFX) panel, Integer.valueOf(index));
        } else if (RenderEngine.OPENGL.equals(renderEngine)) {
            panel = new PanelGL(scene, scene.getWindow().getBaseWidth(), scene.getWindow().getBaseHeight());
            window.getLayeredPane().add((PanelGL) panel, Integer.valueOf(index));
        }
        assert panel != null;
        panel.setSize(new Dimension(scene.getWindow().getBaseWidth(), scene.getWindow().getBaseHeight()));
        return panel;
    }

    public SceneOptions getSceneOptions(Document document) {
        Node node = document.getElementsByTagName("scene").item(0);
        if(node == null) {
            return null;
        }
        AppProperties properties = Dependencies.getAppProperties();
        String title = properties.getProperty("title");
        boolean fullScreen = properties.getPropertyAsBoolean("fullscreen"),
                sameSize = properties.getPropertyAsBoolean("sameSize");
        int renderFPS = properties.getPropertyAsInteger("renderFPS"),
            updateFPS = properties.getPropertyAsInteger("updateFPS"),
            physicsFPS = properties.getPropertyAsInteger("physicsFPS");
        for(int i = 0; i < node.getAttributes().getLength(); i++) {
            Node item = node.getAttributes().item(i);
            switch(item.getNodeName().toUpperCase()) {
                case "TITLE":
                    title = item.getNodeValue();
                    break;
                case "FULLSCREEN":
                    fullScreen = Boolean.parseBoolean(item.getNodeValue());
                    break;
                case "RENDERFPS":
                    renderFPS = Integer.parseInt(item.getNodeValue());
                    break;
                case "UPDATEFPS":
                    updateFPS = Integer.parseInt(item.getNodeValue());
                    break;
                case "PHYSICSFPS":
                    physicsFPS = Integer.parseInt(item.getNodeValue());
                    break;
                case "SAMESIZE":
                    sameSize = Boolean.parseBoolean(item.getNodeValue());
                    break;
            }
        }
        return new SceneOptions(
                title,
                fullScreen,
                renderFPS,
                updateFPS,
                physicsFPS,
                sameSize
        );
    }

    public NodeList getSceneData(Document document) {
        return document.getElementsByTagName("scene").item(0).getChildNodes();
    }

    public Document getDocument(InputStream stream) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(stream);
        document.normalizeDocument();
        return document;
    }

    public void attachSceneData(Scene scene, NodeList sceneData) {
        for(int i = 0; i < sceneData.getLength(); i++) {
            if(sceneData.item(i).getNodeName().startsWith("#")) { // #comment or #text
                continue;
            }
            initNode(scene, sceneData.item(i));
        }
    }

    private void initNode(Scene scene, Node node) {
        String nodeName = node.getNodeName().toLowerCase();
        if("object".equals(nodeName)) {
            String identifier;
            try {
                identifier = node.getAttributes().getNamedItem("id").getNodeValue();
            } catch(Exception e) {
                Logger.exception("Object doesn't have an identifier.", e);
                return;
            }
            GameObject object = scene.createGameObject(identifier);
            if(object == null) {
                Logger.warning("Cannot initialize object with identifier " + identifier + ", skipping its children!");
                return;
            }
            GameObjectDeserializer.deserialize(object, node);
            for(int i = 0; i < node.getChildNodes().getLength(); i++) {
                if(node.getChildNodes().item(i).getNodeName().equals("object")) {
                    Logger.error("Cannot initialize object with identifier " +
                            node.getChildNodes().item(i).getAttributes().getNamedItem("id").getNodeValue() +
                            ". You can't nest object in another object. Use group instead.");
                }
            }
        } else if("styles".equals(nodeName)) {
            String source = "";
            try {
                source = node.getAttributes().getNamedItem("source").getNodeValue();
            } catch(Exception e) {
                Logger.warning("You can use CSS from external source using SOURCE attribute.");
            }
            if(source == null || source.isEmpty()) {
                StyleDeserializer.deserialize(scene, node);
            } else {
                if(!node.getTextContent().isEmpty()) {
                    Logger.warning("Please note - if you're using CSS with external source, you cannot nest another CSS inside. Use additional tag.");
                }
                StyleDeserializer.deserialize(scene, source);
            }
        } else if("scene".equals(nodeName)) {
            String source = "";
            String renderEngine = "";
            try {
                source = node.getAttributes().getNamedItem("source").getNodeValue();
                renderEngine = node.getAttributes().getNamedItem("renderEngine").getNodeValue();
            } catch(Exception e) {
                Logger.warning("Nested scene source is empty.");
            }
            if(source != null && !source.isEmpty()) {
                NestedSceneDeserializer.deserialize(scene, source,
                        renderEngine.isEmpty() ? Dependencies.getAppProperties().getProperty("renderEngine") : renderEngine);
            }
        }
    }

    public Window getWindow() {
        return this.window;
    }

}