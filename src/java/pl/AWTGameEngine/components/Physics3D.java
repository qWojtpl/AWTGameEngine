package pl.AWTGameEngine.components;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.shape.Shape3D;
import pl.AWTGameEngine.annotations.Component3D;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

@Component3D
public class Physics3D extends ObjectComponent {

    private final double G = 9.81;
    private int mass = 10;
    private ColliderType colliderType;
    private final TransformSet velocity = new TransformSet();
    private final GraphicsManager3D graphicsManager3D;

    public Physics3D(GameObject object) {
        super(object);
        graphicsManager3D = ((Panel3D) getWindow().getPanel()).getGraphicsManager3D();
    }

    @Override
    public void onPreUpdate() {
        handleVelocity();
    }

    private void handleVelocity() {
        if(velocity.getX() != 0) {
            getObject().setX(getObject().getX() + velocity.getX());
        }
        if(velocity.getY() != 0) {
            getObject().setY(getObject().getY() + velocity.getY());
        }
        if(velocity.getZ() != 0) {
            getObject().setZ(getObject().getZ() + velocity.getZ());
        }
        velocity.setY(velocity.getY() - (G / getWindow().getUpdateLoop().getFPS()));
    }

    public ColliderType getColliderType() {
        return this.colliderType;
    }

    public void setColliderType(ColliderType colliderType) {
        this.colliderType = colliderType;
    }

    @SerializationSetter
    public void setColliderType(String colliderType) {
        setColliderType(ColliderType.valueOf(colliderType.toUpperCase()));
    }

    public enum ColliderType {
        BOX,
        SPHERE,
        CYLINDER
    }

}
