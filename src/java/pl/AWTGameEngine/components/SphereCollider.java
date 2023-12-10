package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;

public class SphereCollider extends Collider {

    private Path2D path;

    public SphereCollider(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        calculatePoints();
        getColliderRegistry().registerCollider(this);
    }

    @Override
    public void onRemoveComponent() {
        getColliderRegistry().removeCollider(this);
    }

    @Override
    public void onRender(Graphics g) {
        if(!isVisualize()) {
            return;
        }
        g.setColor(visualizeColor.getColor());
        g.drawOval(
                getCamera().parseX(getObject(), getObject().getX() + x),
                getCamera().parseY(getObject(), getObject().getY() + y),
                getCamera().parseScale(getObject().getSizeX() + sizeX),
                getCamera().parseScale(getObject().getSizeY() + sizeY)
        );
    }

    @Override
    public boolean onUpdatePosition(int newX, int newY) {
        calculatePoints();
        return !getColliderRegistry().isColliding(getObject(), this, newX, newY);
    }

    public void calculatePoints() {
        path = calculatePath(getObject().getX(), getObject().getY());
    }

    @Override
    public Path2D calculatePath(int newX, int newY) {
        Path2D path = new Path2D.Double();
        path.append(new Ellipse2D.Double(newX, newY, getObject().getSizeX(), getObject().getSizeY()), true);
        return path;
    }

    @Override
    public Path2D getPath() {
        return path;
    }

}
