package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Parentless;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.engine.NestedPanel;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import java.awt.*;

@Unique
@Parentless
public class ScrollPane extends ObjectComponent {

    private NestedPanel scrollPaneContainer;
    private NestedPanel contentPanel;
    private JScrollPane scrollPane;

    public ScrollPane(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        scrollPaneContainer = new NestedPanel(getWindow());
        scrollPaneContainer.setLayout(new BorderLayout());
        contentPanel = new NestedPanel(getWindow());
        contentPanel.setBackground(Color.WHITE);
        scrollPane = new JScrollPane(contentPanel);
        scrollPaneContainer.add(scrollPane);
        onRender(null);
        getObject().getPanel().add(scrollPaneContainer);
        getPanelRegistry().addPanel(contentPanel);
        getObject().setX(getObject().getX() + getObject().getScaleX());
    }

    @Override
    public void onRender(Graphics g) {
        if(scrollPane == null) {
            return;
        }
        scrollPaneContainer.setLocation(
                (int) ((getObject().getX() - getObject().getScaleX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()));
        scrollPaneContainer.setSize(
                (int) (getObject().getScaleX() * getCamera().getZoom()),
                (int) (getObject().getScaleY() * getCamera().getZoom()));
        scrollPane.setPreferredSize(new Dimension(
                (int) (getObject().getScaleX() * getCamera().getZoom()),
                (int) (getObject().getScaleY() * getCamera().getZoom())));
    }

    @Override
    public void onAddChild(GameObject child) {
        child.setPanel(contentPanel);
        contentPanel.setPreferredSize(new Dimension(
                (int) (getObject().getWidth() * 1.5),
                (int) (getObject().getHeight() * 1.5)));
    }

    @Override
    public void onRemoveChild(GameObject child) {
        child.setPanel(getWindow().getPanel());
    }

}