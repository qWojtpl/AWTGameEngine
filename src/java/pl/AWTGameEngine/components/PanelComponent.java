package pl.AWTGameEngine.components;

import pl.AWTGameEngine.engine.NestedPanel;
import pl.AWTGameEngine.objects.Camera;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

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
    public void onRender(Graphics g) {
        nestedPanel.setLocation(
            getCamera().parseX(getObject(), getObject().getX()),
            getCamera().parseY(getObject(), getObject().getY())
        );
        nestedPanel.setSize(
            getCamera().parseScale(getObject().getSizeX()),
            getCamera().parseScale(getObject().getSizeY())
        );
    }

    @Override
    public void onAddChild(GameObject object) {
        object.setPanel(nestedPanel);
    }

    @Override
    public void onParentChange(GameObject newParent) {
        getObject().getPanel().remove(nestedPanel);
        newParent.getPanel().add(nestedPanel);
    }

    public Camera getPanelCamera() {
        return nestedPanel.getCamera();
    }

}
