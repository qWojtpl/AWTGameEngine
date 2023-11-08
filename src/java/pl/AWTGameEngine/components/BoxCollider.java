package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BoxCollider extends Collider {

    private List<Integer> pointsX = new ArrayList<>();
    private List<Integer> pointsY = new ArrayList<>();
    private Path2D path;

    public BoxCollider(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        calculatePoints(getObject().getRotation());
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
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        if(getObject().getRotation() != 0) {
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(getObject().getRotation()),
                    (getObject().getCenterX() - getCamera().getRelativeX(getObject())) * getCamera().getZoom(),
                    (getObject().getCenterY() - getCamera().getRelativeY(getObject())) * getCamera().getZoom());
            g2d.transform(transform);
        }
        g2d.setColor(visualizeColor);
        g2d.drawRect((int) ((getObject().getX() + x - getCamera().getRelativeX(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getY() + y - getCamera().getRelativeY(getObject())) * getCamera().getZoom()),
                (int) ((getObject().getScaleX() + scaleX) * getCamera().getZoom()),
                (int) ((getObject().getScaleY() + scaleY) * getCamera().getZoom()));
        g2d.setTransform(oldTransform);
    }

    @Override
    public boolean onUpdatePosition(int newX, int newY) {
        calculatePoints(getObject().getRotation());
        return !getColliderRegistry().isColliding(getObject(), this, newX, newY);
    }

    @Override
    public boolean onUpdateRotation(int newRotation) {
        calculatePoints(newRotation);
        return true;
    }

    /**
     Method source:
     <a href="https://gamedev.stackexchange.com/questions/86755/how-to-calculate-corner-positions-marks-of-a-rotated-tilted-rectangle">StackExchange</a>
     */
    public void calculatePoints(int rotation) {
        int[] fixedX = new int[]{0, getObject().getScaleX() + scaleX, getObject().getScaleX() + scaleX, 0};
        int[] fixedY = new int[]{0, 0, getObject().getScaleY() + scaleY, getObject().getScaleY() + scaleY};
        pointsX = new ArrayList<>();
        pointsY = new ArrayList<>();
        path = new Path2D.Double();
        for(int i = 0; i < 4; i++) {
            int tempX = getObject().getX() + x + fixedX[i] - getObject().getCenterX() - x - scaleX / 2;
            int tempY = getObject().getY() + y + fixedY[i] - getObject().getCenterY() - y - scaleY / 2;
            double theta = Math.toRadians(rotation);
            int rotatedX = (int) (tempX * Math.cos(theta) - tempY * Math.sin(theta));
            int rotatedY = (int) (tempX * Math.sin(theta) + tempY * Math.cos(theta));
            int xP = rotatedX + getObject().getCenterX() + x + scaleX / 2;
            int yP = rotatedY + getObject().getCenterY() + y + scaleY / 2;
            pointsX.add(xP);
            pointsY.add(yP);
            if(i == 0) {
                path.moveTo(xP, yP);
            } else {
                path.lineTo(xP, yP);
            }
        }
        path.closePath();
    }

    @Override
    public Path2D calculatePath(int newX, int newY) {
        Path2D path = new Path2D.Double();
        for(int i = 0; i < 4; i++) {
            if(i == 0) {
                path.moveTo(pointsX.get(i) + newX - getObject().getX(), pointsY.get(i) + newY - getObject().getY());
            } else {
                path.lineTo(pointsX.get(i) + newX - getObject().getX(), pointsY.get(i) + newY - getObject().getY());
            }
        }
        return path;
    }

    public List<Integer> getPointsX() {
        return new ArrayList<>(pointsX);
    }

    public List<Integer> getPointsY() {
        return new ArrayList<>(pointsY);
    }

    @Override
    public Path2D getPath() {
        return (Path2D) path.clone();
    }

}
