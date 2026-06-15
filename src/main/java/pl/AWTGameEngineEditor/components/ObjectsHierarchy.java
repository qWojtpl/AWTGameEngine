package pl.AWTGameEngineEditor.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.HTMLComponent;
import pl.AWTGameEngine.engine.enums.RenderEngine;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

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
            return;
        }
        Window detailsWindow = (Window) Dependencies.getWindowsManager().createWindow("scenes/editor/details.xml", RenderEngine.WEB, true);
        detailsWindow.toFront();
        detailsWindow.setResizable(false);
        detailsWindow.setSize(800, 600);
        detailsWindows.put(identifier, detailsWindow);
    }

    @Override
    public void onUpdate() {
        WebGraphicsManager g = ((WebPanel) getScene().getPanel()).getGraphicsManager();
        String feedbacks = g.getFeedbacks(getObject().getIdentifier(), ObjectsHierarchy.class);
        if(feedbacks == null) {
            return;
        }
        showDetails(feedbacks);
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