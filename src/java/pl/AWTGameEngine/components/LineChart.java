package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public class LineChart extends ObjectComponent {

    public LineChart(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY()),
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY() + getObject().getScaleY())
        );
        g.drawLine(
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY() + getObject().getScaleY()),
                getCamera().parseX(getObject(), getObject().getX() + getObject().getScaleX()),
                getCamera().parseY(getObject(), getObject().getY() + getObject().getScaleY())
        );
    }

}
