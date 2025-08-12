package pl.AWTGameEngine.components;

import pl.AWTGameEngine.engine.graphics.GraphicsManager;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class BoxCollider extends Collider {

    private List<Double> pointsX = new ArrayList<>();
    private List<Double> pointsY = new ArrayList<>();
    private Path2D path;

    public BoxCollider(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        calculatePoints(getObject().getX(), getObject().getY(), getObject().getRotation().getX());
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
                getCamera().parsePlainValue(getObject().getSizeX() + sizeX),
                getCamera().parsePlainValue(getObject().getSizeY() + sizeY),
                new GraphicsManager.RenderOptions()
                        .setColor(visualizeColor.getColor())
                        .setRotation(getObject().getRotation().getX())
                        .setRotationCenterX(getCamera().parseX(getObject(), getObject().getCenterX()))
                        .setRotationCenterY(getCamera().parseY(getObject(), getObject().getCenterY()))
        );
    }

    @Override
    public boolean onUpdatePosition(double newX, double newY) {
        calculatePoints(newX, newY, getObject().getRotation().getX());
        return !getColliderRegistry().isColliding(getObject(), this, newX, newY);
    }

    @Override
    public boolean onUpdateRotation(double newRotation) {
        calculatePoints(getObject().getX(), getObject().getY(), newRotation);
        return !getColliderRegistry().isColliding(getObject(), this, getObject().getX(), getObject().getY());
    }

    /**
     Method source:
     <a href="https://gamedev.stackexchange.com/questions/86755/how-to-calculate-corner-positions-marks-of-a-rotated-tilted-rectangle">StackExchange</a>
     */
    public void calculatePoints(double objX, double objY, double rotation) {
        double[] fixedX = new double[]{0, getObject().getSizeX() + sizeX, getObject().getSizeX() + sizeX, 0};
        double[] fixedY = new double[]{0, 0, getObject().getSizeY() + sizeY, getObject().getSizeY() + sizeY};
        pointsX = new ArrayList<>();
        pointsY = new ArrayList<>();
        path = new Path2D.Double();
        for(int i = 0; i < 4; i++) {
            double tempX = objX + x + fixedX[i] - (objX + getObject().getSizeX() / 2) - x - sizeX / 2;
            double tempY = objY + y + fixedY[i] - (objY + getObject().getSizeY() / 2) - y - sizeY / 2;
            double theta = Math.toRadians(rotation);
            double rotatedX = Math.round(tempX * Math.cos(theta) - tempY * Math.sin(theta));
            double rotatedY = Math.round(tempX * Math.sin(theta) + tempY * Math.cos(theta));
            double xP = rotatedX + (objX + getObject().getSizeX() / 2) + x + sizeX / 2;
            double yP = rotatedY + (objY + getObject().getSizeY() / 2) + y + sizeY / 2;
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
    public Path2D calculatePath(double newX, double newY) {
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

    public List<Double> getPointsX() {
        return new ArrayList<>(pointsX);
    }

    public List<Double> getPointsY() {
        return new ArrayList<>(pointsY);
    }

    @Override
    public Path2D getPath() {
        return (Path2D) path.clone();
    }

}
