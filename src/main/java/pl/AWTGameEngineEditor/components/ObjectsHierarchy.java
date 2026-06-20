package pl.AWTGameEngineEditor.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.HTMLFileComponent;
import pl.AWTGameEngine.engine.WaitForSeconds;
import pl.AWTGameEngine.engine.enums.RenderEngine;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;
import pl.AWTGameEngineEditor.components.details.ObjectDetails;
import pl.AWTGameEngineEditor.manager.EditorManager;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@WebComponent
public class ObjectsHierarchy extends HTMLFileComponent {

    private boolean locked = false;
    private String lastRenderString = "";
    private final HashMap<String, Window> detailsWindows = new HashMap<>();

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

    private String loadObjects(String renderString) {
        locked = true;
        StringBuilder builder = new StringBuilder();
        List<GameObject> objects = EditorManager.getInstance().getGameViewWindow().getCurrentScene().getGameObjects();
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
        GameObject go = EditorManager.getInstance().getGameViewWindow().getCurrentScene().getGameObjectByName(identifier);
        Window detailsWindow = (Window) Dependencies.getWindowsManager().createWindow("scenes/editor/details.xml", RenderEngine.WEB, true);
        detailsWindow.setResizable(false);
        detailsWindow.setSize(800, 600);
        detailsWindow.setRatio(4, 3);
        detailsWindow.toFront();
        detailsWindows.put(identifier, detailsWindow);
        ObjectDetails details = ((ObjectDetails) detailsWindow.getCurrentScene().getGameObjectByName("object-details").getComponentByClass(ObjectDetails.class));
        new WaitForSeconds(0.5).here();
        details.setup(identifier);
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
    public String filterRender(String data) {
        if(locked) {
            return lastRenderString;
        }
        lastRenderString = loadObjects(data);
        return lastRenderString;
    }

}