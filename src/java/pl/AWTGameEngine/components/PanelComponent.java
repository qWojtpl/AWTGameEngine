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
        nestedPanel = new NestedPanel(getWindow());
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
        nestedPanel.setPreferredSize(new Dimension(
            getCamera().parseScale(getObject().getSizeX()),
            getCamera().parseScale(getObject().getSizeY())
        ));
    }

    public Camera getPanelCamera() {
        return nestedPanel.getCamera();
    }

}
