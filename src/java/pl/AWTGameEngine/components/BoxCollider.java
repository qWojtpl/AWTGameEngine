package pl.AWTGameEngine.components;

import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.geom.Path2D;
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
        calculatePoints(getObject().getX(), getObject().getY(), getObject().getRotationX());
        getColliderRegistry().registerCollider(this);
    }

    @Override
    public void onRemoveComponent() {
        getColliderRegistry().removeCollider(this);
    }

    @Override
    public void onRender(GraphicsManager g) {
        if(!isVisualize()) {
            return;
        }
        g.drawRect(
                getCamera().parseX(getObject(), getObject().getX() + x),
                getCamera().parseY(getObject(), getObject().getY() + y),
                getCamera().parseScale(getObject().getSizeX() + sizeX),
                getCamera().parseScale(getObject().getSizeY() + sizeY),
                new GraphicsManager.RenderOptions()
                        .setColor(visualizeColor.getColor())
                        .setRotation(getObject().getRotationX())
                        .setRotationCenterX(getCamera().parseX(getObject(), getObject().getCenterX()))
                        .setRotationCenterY(getCamera().parseY(getObject(), getObject().getCenterY()))
        );
    }

    @Override
    public boolean onUpdatePosition(int newX, int newY) {
        calculatePoints(newX, newY, getObject().getRotationX());
        return !getColliderRegistry().isColliding(getObject(), this, newX, newY);
    }

    @Override
    public boolean onUpdateRotation(int newRotation) {
        calculatePoints(getObject().getX(), getObject().getY(), newRotation);
        return !getColliderRegistry().isColliding(getObject(), this, getObject().getX(), getObject().getY());
    }

    /**
     Method source:
     <a href="https://gamedev.stackexchange.com/questions/86755/how-to-calculate-corner-positions-marks-of-a-rotated-tilted-rectangle">StackExchange</a>
     */
    public void calculatePoints(int objX, int objY, int rotation) {
        int[] fixedX = new int[]{0, getObject().getSizeX() + sizeX, getObject().getSizeX() + sizeX, 0};
        int[] fixedY = new int[]{0, 0, getObject().getSizeY() + sizeY, getObject().getSizeY() + sizeY};
        pointsX = new ArrayList<>();
        pointsY = new ArrayList<>();
        path = new Path2D.Double();
        for(int i = 0; i < 4; i++) {
            int tempX = objX + x + fixedX[i] - (objX + getObject().getSizeX() / 2) - x - sizeX / 2;
            int tempY = objY + y + fixedY[i] - (objY + getObject().getSizeY() / 2) - y - sizeY / 2;
            double theta = Math.toRadians(rotation);
            int rotatedX = (int) Math.round(tempX * Math.cos(theta) - tempY * Math.sin(theta));
            int rotatedY = (int) Math.round(tempX * Math.sin(theta) + tempY * Math.cos(theta));
            int xP = rotatedX + (objX + getObject().getSizeX() / 2) + x + sizeX / 2;
            int yP = rotatedY + (objY + getObject().getSizeY() / 2) + y + sizeY / 2;
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
