package pl.AWTGameEngineEditor.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.HTMLComponent;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

@WebComponent
public class ObjectsHierarchy extends HTMLComponent {

    private boolean locked = false;
    private String lastRenderString = "";

    public ObjectsHierarchy(GameObject object) {
        super(object);
    }

    @Override
    public void onEverySecond() {
        locked = false;
    }

    private Window getGameViewWindow() {
        return ((GameView) getScene().getGameObjectByName("game-view").getComponentByClass(GameView.class)).getGameViewWindow();
    }

    private String loadObjects(String renderString) {
        locked = true;
        StringBuilder builder = new StringBuilder();
        for(GameObject go : getGameViewWindow().getCurrentScene().getGameObjects()) {
            builder
                    .append("<option value=\"")
                    .append(go.getIdentifier())
                    .append("\">")
                    .append(go.getIdentifier())
                    .append("</option>\n");
        }
        return renderString.replaceAll("\\{\\{objects}}", builder.toString());
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