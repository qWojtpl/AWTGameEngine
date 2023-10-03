package pl.AWTGameEngine.components;

import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.objects.GameObject;

import javax.swing.*;
import java.awt.*;

@Unique
public class ScrollPane extends ObjectComponent {

    private JScrollPane scrollPane;

    public ScrollPane(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        scrollPane = new JScrollPane();
        scrollPane.add(new Label("test"));
        scrollPane.setVisible(true);
        getWindow().getPanel().add(scrollPane);
    }

    @Override
    public void onRender(Graphics g) {
        if(scrollPane == null) {
            return;
        }
/*        System.out.println(getObject().getX());
        System.out.println(getObject().getY());*/
/*        scrollPane.setLocation((int) ((getObject().getX() - getObject().getScaleX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom()));
        scrollPane.setSize((int) (getObject().getScaleX() * getCamera().getZoom()),
                (int) (getObject().getScaleY() * getCamera().getZoom()));
        scrollPane.setVisible(true);*/
    }

}
