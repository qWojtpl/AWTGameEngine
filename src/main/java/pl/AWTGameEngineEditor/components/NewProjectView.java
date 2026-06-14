package pl.AWTGameEngineEditor.components;

import javafx.application.Platform;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.base.HTMLComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;

@WebComponent
public class NewProjectView extends HTMLComponent {

    private boolean initialized = false;

    public NewProjectView(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        getWindow().setResizable(false);
        getWindow().setSameSize(false);
        getWindow().setSize(300, 500);
    }

    @Override
    public void onUpdate() {
        if(!initialized) {
            return;
        }
        WebGraphicsManager manager = ((WebPanel) getObject().getPanel()).getGraphicsManager();
        if(manager != null) {
            System.out.println(manager.executeGetResult("document.getElementById('x-project-name').value"));
        }
    }

    @Override
    public String getRenderString() {
        if(!initialized) {
            initialized = true;
        }
        return String.join("\n", Dependencies.getResourceManager().getResource("webview/editor/newProject.html"));
    }

}
