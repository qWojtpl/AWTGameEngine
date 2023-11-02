package pl.AWTGameEngine.components;

import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BoxCollider extends ObjectComponent {

    private int x = 0;
    private int y = 0;
    private int scaleX = 0;
    private int scaleY = 0;
    private boolean visualize = false;
    private Color visualizeColor = Color.GREEN;
    private List<Integer> pointsX = new ArrayList<>();
    private List<Integer> pointsY = new ArrayList<>();
    private Path2D path;

    public BoxCollider(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        calculatePoints(getObject().getRotation());
        getColliderRegistry().registerCollider(this, getObject());
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
        if(getColliderRegistry().isColliding(getObject(), this, newX, newY)) {
            for(ObjectComponent component : getObject().getComponents()) {
                component.onCollide();
            }
            return false;
        }
        return true;
    }

    /**
     Method source:
     <a href="https://gamedev.stackexchange.com/questions/86755/how-to-calculate-corner-positions-marks-of-a-rotated-tilted-rectangle">StackExchange</a>
     */
    @Override
    public boolean onUpdateRotation(int newRotation) {
        calculatePoints(newRotation);
        return true;
    }

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

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getScaleX() {
        return this.scaleX;
    }

    public int getScaleY() {
        return this.scaleY;
    }

    public boolean isVisualize() {
        return this.visualize;
    }

    public Color getVisualizeColor() {
        return this.visualizeColor;
    }

    public List<Integer> getPointsX() {
        return new ArrayList<>(pointsX);
    }

    public List<Integer> getPointsY() {
        return new ArrayList<>(pointsY);
    }

    public Path2D getPath() {
        return (Path2D) path.clone();
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setX(String x) {
        setX(Integer.parseInt(x));
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setY(String y) {
        setY(Integer.parseInt(y));
    }

    public void setScaleX(int scaleX) {
        this.scaleX = scaleX;
    }

    public void setScaleX(String scaleX) {
        setScaleX(Integer.parseInt(scaleX));
    }

    public void setScaleY(int scaleY) {
        this.scaleY = scaleY;
    }

    public void setScaleY(String scaleY) {
        setScaleY(Integer.parseInt(scaleY));
    }

    public void setVisualize(boolean visualize) {
        this.visualize = visualize;
    }

    public void setVisualize(String visualize) {
        setVisualize(Boolean.parseBoolean(visualize));
    }

    public void setVisualizeColor(Color color) {
        this.visualizeColor = color;
    }

    public void setVisualizeColor(String color) {
        Color c;
        try {
            Field field = Class.forName("java.awt.Color").getField(color.toLowerCase());
            c = (Color) field.get(null);
        } catch (Exception e) {
            c = Color.BLACK;
        }
        setVisualizeColor(c);
    }

}
