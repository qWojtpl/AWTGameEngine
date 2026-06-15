package pl.AWTGameEngineEditor.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.HTMLComponent;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.enums.RenderEngine;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@WebComponent
public class ObjectsHierarchy extends HTMLComponent {

    private boolean locked = false;
    private String lastRenderString = "";
    private HashMap<String, Window> detailsWindows = new HashMap<>();

    public ObjectsHierarchy(GameObject object) {
        super(object);
    }

    @Override
    public void onEverySecond() {
        locked = false;
    }

    public void releaseLock() {
        locked = false;
    }

    private Window getGameViewWindow() {
        return ((GameView) getScene().getGameObjectByName("game-view").getComponentByClass(GameView.class)).getGameViewWindow();
    }

    private String loadObjects(String renderString) {
        locked = true;
        StringBuilder builder = new StringBuilder();
        List<GameObject> objects = getGameViewWindow().getCurrentScene().getGameObjects();
        objects.sort(Comparator.comparing(GameObject::getIdentifier));
        for(GameObject go : objects) {
            builder
                    .append("<option value=\"")
                    .append(go.getIdentifier())
                    .append("\" ondblclick=\"").append("feedback('objects-hierarchy', 'pl.AWTGameEngineEditor.components.ObjectsHierarchy', '")
                    .append(go.getIdentifier())
                    .append("')\">")
                    .append(go.getIdentifier())
                    .append("</option>\n");
        }
        return renderString.replaceAll("\\{\\{objects}}", builder.toString());
    }

    private void showDetails(String identifier) {
        if(detailsWindows.containsKey(identifier)) {
            if(detailsWindows.get(identifier).getWindowListener().isOpened()) {
                detailsWindows.get(identifier).toFront();
                return;
            }
        }
        GameObject go = getGameViewWindow().getCurrentScene().getGameObjectByName(identifier);
        Window detailsWindow = (Window) Dependencies.getWindowsManager().createWindow("scenes/editor/details.xml", RenderEngine.WEB, true);
        detailsWindow.setResizable(false);
        detailsWindow.setSize(800, 600);
        detailsWindow.setRatio(4, 3);
        detailsWindow.toFront();
        detailsWindows.put(identifier, detailsWindow);
        WebGraphicsManager detailsGraphicsManager = ((WebPanel) detailsWindow.getCurrentScene().getPanel()).getGraphicsManager();
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-identifier').value=\"%s\"", identifier));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-position-x').value=%s", go.getPosition().getX()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-position-y').value=%s", go.getPosition().getY()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-position-z').value=%s", go.getPosition().getZ()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-size-x').value=%s", go.getSize().getX()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-size-y').value=%s", go.getSize().getY()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-size-z').value=%s", go.getSize().getZ()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-rotation-x').value=%s", go.getRotation().getZ()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-rotation-y').value=%s", go.getRotation().getZ()));
        detailsGraphicsManager.execute(String.format("document.getElementById('objectdetails-rotation-z').value=%s", go.getRotation().getZ()));
    }

    @Override
    public void onUpdate() {
        checkDetailsFeedbacks();
        WebGraphicsManager g = ((WebPanel) getScene().getPanel()).getGraphicsManager();
        String feedbacks = g.getFeedbacks(getObject().getIdentifier(), ObjectsHierarchy.class);
        if(feedbacks == null) {
            return;
        }
        showDetails(feedbacks);
    }

    private void checkDetailsFeedbacks() {
        for(String identifier : detailsWindows.keySet()) {
            if(detailsWindows.get(identifier).getCurrentScene() == null) {
                continue;
            }
            WebGraphicsManager detailsGraphicsManager = ((WebPanel) detailsWindows.get(identifier).getCurrentScene().getPanel()).getGraphicsManager();
            String feedback = detailsGraphicsManager.getFeedbacks(getObject().getIdentifier(), ObjectsHierarchy.class);
            if(feedback == null) {
                continue;
            }
            GameObject go = getGameViewWindow().getCurrentScene().getGameObjectByName(identifier);
            String[] split = feedback.split(":");
            switch(split[0]) {
                case "pos.x":
                    go.getPosition().setX(Double.parseDouble(split[1]));
                    break;
                case "pos.y":
                    go.getPosition().setY(Double.parseDouble(split[1]));
                    break;
                case "pos.z":
                    go.getPosition().setZ(Double.parseDouble(split[1]));
                    break;
                case "size.x":
                    go.getSize().setX(Double.parseDouble(split[1]));
                    break;
                case "size.y":
                    go.getSize().setY(Double.parseDouble(split[1]));
                    break;
                case "size.z":
                    go.getSize().setZ(Double.parseDouble(split[1]));
                    break;
                case "rotation.x":
                    go.setRotation(go.getRotation().setX(Double.parseDouble(split[1])));
                    break;
                case "rotation.y":
                    go.setRotation(go.getRotation().setY(Double.parseDouble(split[1])));
                    break;
                case "rotation.z":
                    go.setRotation(go.getRotation().setZ(Double.parseDouble(split[1])));
                    break;
            }
        }
    }

    @Override
    public String getRenderString() {
        if(locked) {
            return lastRenderString;
        }
        String renderString = String.join("\n", Dependencies.getResourceManager().getResource("webview/editor/objectshierarchy.html"));
        lastRenderString = loadObjects(renderString);
        return lastRenderString;
    }

}