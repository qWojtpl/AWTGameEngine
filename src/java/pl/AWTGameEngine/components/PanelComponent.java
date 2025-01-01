package pl.AWTGameEngine.components;

import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.engine.panels.NestedPanel;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;

public class PanelComponent extends ObjectComponent {

    private NestedPanel nestedPanel;

    public PanelComponent(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        nestedPanel = new NestedPanel(getObject());
        getObject().getPanel().add(nestedPanel);
        getPanelRegistry().addPanel(nestedPanel);
    }

    @Override
    public void onRemoveComponent() {
        getObject().getPanel().remove(nestedPanel);
        getPanelRegistry().removePanel(nestedPanel);
    }

    @Override
    public void onRender(GraphicsManager g) {
        if(nestedPanel == null) {
            return;
        }
        nestedPanel.setLocation(
            getCamera().parseX(getObject(), getObject().getX()),
            getCamera().parseY(getObject(), getObject().getY())
        );
        nestedPanel.setSize(
            getCamera().parsePlainValue(getObject().getSizeX()),
            getCamera().parsePlainValue(getObject().getSizeY())
        );
    }

    public NestedPanel getNestedPanel() {
        return this.nestedPanel;
    }

    public Camera getPanelCamera() {
        return nestedPanel.getCamera();
    }

}
