package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;

public class SphereCollider extends Collider {

    public SphereCollider(GameObject object) {
        super(object);
    }

    @Override
    public void onRender(Graphics g) {
        if(!isVisualize()) {
            return;
        }
        g.setColor(visualizeColor);
        g.drawOval((int) ((getObject().getX() + x - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() + y - getCamera().getRelativeY(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getScaleX() + scaleX) * getCamera().getZoom()),
                (int) ((getObject().getScaleY() + scaleY) * getCamera().getZoom()));
    }

    @Override
    public boolean onUpdatePosition(int newX, int newY) {
/*        if(getColliderRegistry().isColliding(getObject(), this, newX, newY)) {
            for(ObjectComponent component : getObject().getComponents()) {
                component.onCollide();
            }
            return false;
        }*/
        return true;
    }

}
