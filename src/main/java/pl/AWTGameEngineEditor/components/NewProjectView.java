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

    private boolean declared = false;
    private boolean goBackLoading = false;

    public NewProjectView(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        //getWindow().setResizable(false);
        getWindow().setSameSize(false);
        getWindow().setSize(300, 500);
    }

    @Override
    public void onUpdate() {
        WebGraphicsManager manager = ((WebPanel) getObject().getPanel()).getGraphicsManager();
        if(manager != null) {
            Platform.runLater(() -> {
                String name = String.valueOf(manager.getWebView().getEngine().executeScript("document.getElementById('x-project-name').value"));
            });
        }
    }

    @Override
    public String getRenderString() {
        return String.join("\n", Dependencies.getResourceManager().getResource("webview/editor/newProject.html"));
    }

}
