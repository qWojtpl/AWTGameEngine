package pl.AWTGameEngineEditor.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.BlankRenderer;
import pl.AWTGameEngine.components.TextRenderer;
import pl.AWTGameEngine.components.base.HTMLComponent;
import pl.AWTGameEngine.components.base.HTMLFileComponent;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;
import pl.AWTGameEngineEditor.manager.EditorManager;

@WebComponent
public class MenuBar extends HTMLFileComponent {

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
        if(feedbacks.equals("addGameObject")) {
            GameObject object = EditorManager.getInstance().getGameViewWindow().getCurrentScene().createGameObject("test" + i++);
            object.getSize().setX(100).setY(100);
            object.getPosition().setX(i * 100).setY(i * 100);
            BlankRenderer renderer = new BlankRenderer(object);
            renderer.setColor("green");
            object.addComponent(renderer);
            TextRenderer textRenderer = new TextRenderer(object);
            object.addComponent(textRenderer);
            ((ObjectsHierarchy) getScene().getGameObjectByName("objects-hierarchy").getComponentByClass(ObjectsHierarchy.class)).releaseLock();
        }
    }

}
