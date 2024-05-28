package pl.AWTGameEngine.scenes;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.InputStream;

public class SceneLoader {

    private final Window window;

    public SceneLoader(Window window) {
        this.window = window;
    }

    public void loadSceneFile(String scenePath) {
        Logger.log(2, "Loading scene: " + scenePath);
        ResourceManager resourceManager = Dependencies.getResourceManager();
        AppProperties appProperties = Dependencies.getAppProperties();
        if(window.getCurrentScene() != null) {
            Scene scene = window.getCurrentScene();
            window.setCurrentScene(null);
            scene.removeAllObjects();
            resourceManager.clearAudioClips();
        }
        try(InputStream sceneStream = resourceManager.getResourceAsStream(scenePath)) {
            Document document = getDocument(sceneStream);
            SceneOptions sceneOptions = getSceneOptions(document);
            String title;
            double multiplier;
            if(sceneOptions != null) {
                title = sceneOptions.getTitle();
                window.getRenderLoop().setFPS(sceneOptions.getRenderFPS());
                window.getUpdateLoop().setFPS(sceneOptions.getUpdateFPS());
                multiplier = sceneOptions.getMultiplier();
                if(sceneOptions.isFullscreen()) {
                    window.setFullScreen(true);
                }
            } else {
                title = appProperties.getProperty("title");
                multiplier = appProperties.getPropertyAsDouble("multiplier");
                window.setFullScreen(appProperties.getPropertyAsBoolean("fullscreen"));
            }
            window.setTitle(title);
            window.setCurrentScene(new Scene(scenePath, window));
            window.setMultiplier(multiplier);
            window.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            window.getPanel().removeAll();
            window.getCurrentScene().getPanelRegistry().addPanel(window.getPanel());
            window.setLocationRelativeTo(null);
            NodeList data = getSceneData(document);
            if(data == null) {
                return;
            }
            window.getCurrentScene().setCustomStyles(getCustomStyles(document));
            attachSceneData(data);
        } catch(Exception e) {
            Logger.log("Cannot load scene " + scenePath, e);
            return;
        }
        window.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        Logger.log(2, "Scene loaded.");
    }

    public SceneOptions getSceneOptions(Document document) {
        Node node = document.getElementsByTagName("Scene").item(0);
        if(node == null) {
            return null;
        }
        String title = "Default window title";
        boolean fullScreen = false;
        int renderFPS = 60, updateFPS = 60, multiplier = 3;
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
                case "MULTIPLIER":
                    multiplier = Integer.parseInt(item.getNodeValue());
                    break;
            }
        }
        return new SceneOptions(
                title,
                fullScreen,
                renderFPS,
                updateFPS,
                multiplier
        );
    }

    public NodeList getSceneData(Document document) {
        return document.getElementsByTagName("Object");
    }

    public String getCustomStyles(Document document) {
        Node node = document.getElementsByTagName("Styles").item(0);
        if(node == null) {
            return "";
        }
        return node.getTextContent();
    }

    public Document getDocument(InputStream stream) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(stream);
        document.normalizeDocument();
        return document;
    }

    public void attachSceneData(NodeList sceneData) {
        attachSceneData(sceneData, null);
    }

    public void attachSceneData(NodeList sceneData, GameObject defaultParent) {
        for(int i = 0; i < sceneData.getLength(); i++) {
            GameObject object = window.getCurrentScene().createGameObject(
                    sceneData.item(i).getAttributes().getNamedItem("id").getNodeValue());
            object.deserialize(sceneData.item(i));
            if(object.getParent() == null) {
                object.setParent(defaultParent);
            }
        }
    }

    public Window getWindow() {
        return this.window;
    }

}