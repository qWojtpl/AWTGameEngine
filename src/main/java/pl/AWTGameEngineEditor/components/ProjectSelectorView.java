package pl.AWTGameEngineEditor.components;

import javafx.application.Platform;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.base.HTMLComponent;
import pl.AWTGameEngine.engine.RenderEngine;
import pl.AWTGameEngine.engine.graphics.WebGraphicsManager;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.windows.Window;
import pl.AWTGameEngineEditor.managers.EditorProjectManager;

@WebComponent
@Unique
public class ProjectSelectorView extends HTMLComponent {

    private final EditorProjectManager editorProjectManager = EditorProjectManager.getInstance();

    private boolean declared = false;
    private Window newProjectWindow = null;

    public ProjectSelectorView(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        getWindow().setResizable(false);
        getWindow().setSize(800, 450);
    }

    @Override
    public void onUpdate() {
        if(newProjectWindow != null) {
            if(newProjectWindow.isVisible()) {
                return;
            }
        }
        WebGraphicsManager manager = ((WebPanel) getObject().getPanel()).getGraphicsManager();
        if(manager != null) {
            if(!declared) {
                manager.declareVariable("createNewProjectClick", "false");
                declared = true;
            }
            Platform.runLater(() -> {
                String selected = String.valueOf(manager.getWebView().getEngine().executeScript("createNewProjectClick"));
                if(selected.equals("true")) {
                    loadNewProject();
                    declared = false;
                }
            });
        }
    }

    private void loadNewProject() {
        newProjectWindow = Dependencies.getWindowsManager().createWindow("scenes/editor/newProject.xml", RenderEngine.WEB);
    }

    @Override
    public String getRenderString() {
        return String.join("\n", Dependencies.getResourceManager().getResource("webview/editor/projectSelector.html"));
    }

}
