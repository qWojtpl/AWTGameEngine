package pl.AWTGameEngineEditor.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.BlankRenderer;
import pl.AWTGameEngine.components.base.HTMLComponent;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;

@WebComponent
public class MenuBar extends HTMLComponent {

    public MenuBar(GameObject object) {
        super(object);
    }

    private int i = 0;

    @Override
    public void onUpdate() {
        WebGraphicsManager g = ((WebPanel) getScene().getPanel()).getGraphicsManager();
        String feedbacks = g.getFeedbacks(getObject().getIdentifier(), MenuBar.class);
        if(feedbacks == null) {
            return;
        }
        if(feedbacks.contains("addGameObject")) {
            GameObject object = getGameViewWindow().getCurrentScene().createGameObject("test" + i++);
            object.getSize().setX(100).setY(100);
            object.getPosition().setX(i * 100).setY(i * 100);
            BlankRenderer renderer = new BlankRenderer(object);
            renderer.setColor("green");
            object.addComponent(renderer);
            ((ObjectsHierarchy) getScene().getGameObjectByName("objects-hierarchy").getComponentByClass(ObjectsHierarchy.class)).releaseLock();
        }
    }

    @Override
    public String getRenderString() {
        return String.join("\n", Dependencies.getResourceManager().getResource("webview/editor/menubar.html"));
    }

    private Window getGameViewWindow() {
        return ((GameView) getScene().getGameObjectByName("game-view").getComponentByClass(GameView.class)).getGameViewWindow();
    }

}
