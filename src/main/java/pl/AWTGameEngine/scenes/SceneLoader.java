package pl.AWTGameEngine.scenes;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.AppProperties;
import pl.AWTGameEngine.engine.GameObjectDeserializer;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.ResourceManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class SceneLoader {

    private final Window window;

    public SceneLoader(Window window) {
        this.window = window;
    }

    public void loadSceneFile(String scenePath) {
        Logger.info("Loading scene: " + scenePath);
        ResourceManager resourceManager = Dependencies.getResourceManager();
        AppProperties appProperties = Dependencies.getAppProperties();
        if(window.getCurrentScene() != null) {
            window.unloadScene();
        }
        try(InputStream sceneStream = resourceManager.getResourceAsStream(scenePath)) {
            Document document = getDocument(sceneStream);
            SceneOptions sceneOptions = getSceneOptions(document);
            String title;
            boolean sameSize;
            if(sceneOptions != null) {
                title = sceneOptions.getTitle();
                window.getRenderLoop().setFPS(sceneOptions.getRenderFPS());
                window.getUpdateLoop().setFPS(sceneOptions.getUpdateFPS());
                window.getPhysicsLoop().setFPS(sceneOptions.getPhysicsFPS());
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
            window.setCurrentScene(new Scene(scenePath, window));
            window.setSameSize(sameSize);
            window.setLocationRelativeTo(null);
            NodeList data = getSceneData(document);
            if(data == null) {
                return;
            }
            window.getCurrentScene().setCustomStyles(getCustomStyles(document));
            attachSceneData(data);
        } catch(Exception e) {
            Logger.exception("Cannot load scene " + scenePath, e);
            return;
        }
        Logger.info("Scene loaded.");
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
        return document.getElementsByTagName("object");
    }

    public String getCustomStyles(Document document) {
        Node node = document.getElementsByTagName("styles").item(0);
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
        for(int i = 0; i < sceneData.getLength(); i++) {
            if(!sceneData.item(i).getParentNode().getNodeName().equals("scene")) {
                continue;
            }
            initObject(sceneData.item(i));
        }
    }

    private void initObject(Node node) {
        String identifier;
        try {
             identifier = node.getAttributes().getNamedItem("id").getNodeValue();
        } catch(Exception e) {
            Logger.exception("Object doesn't have an identifier.", e);
            return;
        }
        GameObject object = window.getCurrentScene().createGameObject(identifier);
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
    }

    public Window getWindow() {
        return this.window;
    }

}