package pl.AWTGameEngine.scenes;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SceneLoader {

    private final Window window;

    public SceneLoader(Window window) {
        this.window = window;
    }

    public void loadSceneFile(String scenePath) {
        Logger.log(2, "Loading scene: " + scenePath);
        if(window.getCurrentScene() != null) {
            Scene scene = window.getCurrentScene();
            window.setCurrentScene(null);
            scene.removeAllObjects();
            ResourceManager.clearAudioClips();
        }
        Properties customProperties = AppProperties.getCustomProperties(getScenePropertiesPath(scenePath));
        String title;
        double multiplier;
        if(customProperties != null) {
            title = AppProperties.getProperty("title", customProperties);
            window.getRenderLoop().setFPS(AppProperties.getPropertyAsInteger("renderFps", customProperties));
            window.getUpdateLoop().setFPS(AppProperties.getPropertyAsInteger("updateFps", customProperties));
            if(AppProperties.getProperty("multiplier", customProperties) != null) {
                multiplier = AppProperties.getPropertyAsDouble("multiplier", customProperties);
            } else {
                multiplier = AppProperties.getPropertyAsDouble("multiplier");
            }
        } else {
            title = AppProperties.getProperty("title");
            multiplier = AppProperties.getPropertyAsDouble("multiplier");
        }
        window.setTitle(title);
        window.setCurrentScene(new Scene(scenePath, window));
        window.setMultiplier(multiplier);
        window.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        window.getPanel().removeAll();
        window.getCurrentScene().getPanelRegistry().addPanel(window.getPanel());
        window.setLocationRelativeTo(null);
        NodeList data;
        try(InputStream sceneStream = ResourceManager.getResourceAsStream(scenePath)) {
            Document document = getDocument(sceneStream);
            data = getSceneData(document);
            if(data == null) {
                return;
            }
            window.getCurrentScene().setCustomStyles(getCustomStyles(document));
        } catch(Exception e) {
            Logger.log("Cannot load scene " + scenePath, e);
            return;
        }
        attachSceneData(data);
        window.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        Logger.log(2, "Scene loaded.");
    }

    public NodeList getSceneData(Document document) {
        return document.getElementsByTagName("Object");
    }

    public String getCustomStyles(Document document) {
        Node node = document.getElementsByTagName("SceneStyles").item(0);
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

    public static String getScenePropertiesPath(String scenePath) {
        StringBuilder path = new StringBuilder();
        for(int i = 0; i < scenePath.length() - 3; i++) {
            path.append(scenePath.charAt(i));
        }
        path.append("properties");
        return path.toString();
    }

    public Window getWindow() {
        return this.window;
    }

}