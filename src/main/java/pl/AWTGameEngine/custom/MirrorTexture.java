package pl.AWTGameEngine.custom;

import javafx.application.Platform;
import javafx.scene.*;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Rotate;
import pl.AWTGameEngine.annotations.Component3D;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.Renderable3D;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.objects.TransformSet;

@Component3D
public class MirrorTexture extends ObjectComponent implements Renderable3D {

    private Base3DShape target;
    private Panel3D panel;
    private int counter = 0;
    private int divider = 10;
    private double nearClip = 0.1;
    private double farClip = 1000;
    private TransformSet position = new TransformSet();
    private TransformSet rotation = new TransformSet();

    public MirrorTexture(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        if(target == null) {
            setTarget(getObject().getIdentifier());
        }
        panel = (Panel3D) getPanel();
    }

    @Override
    public void on3DRenderRequest(GraphicsManager3D g) {
        if(target == null) {
            return;
        }
        if(counter != divider) {
            counter++;
            return;
        }
        counter = 0;
        Platform.runLater(() -> {
            Camera oldCamera = panel.getFxScene().getCamera();

            // init new camera
            PerspectiveCamera newCamera = new PerspectiveCamera(true);
            newCamera.setNearClip(nearClip);
            newCamera.setFarClip(farClip);

            // init camera position
            Group cameraPitch = new Group();
            Group cameraYaw = new Group();
            cameraPitch.getChildren().add(newCamera);
            cameraYaw.getChildren().add(cameraPitch);
            panel.getRootGroup().getChildren().add(cameraYaw);

            cameraPitch.setTranslateX(position.getX());
            cameraPitch.setTranslateY(-position.getY());
            cameraPitch.setTranslateZ(position.getZ());

            cameraPitch.setRotationAxis(Rotate.X_AXIS);
            cameraPitch.setRotate(rotation.getX());
            cameraYaw.setRotationAxis(Rotate.Y_AXIS);
            cameraYaw.setRotate(rotation.getY());
            newCamera.setRotationAxis(Rotate.Z_AXIS);
            newCamera.setRotate(rotation.getZ());

            // take a snapshot
            panel.getFxScene().setCamera(newCamera);
            WritableImage writableImage = panel.getFxScene().snapshot(null);

            // rollback
            panel.getRootGroup().getChildren().remove(cameraYaw);
            panel.getFxScene().setCamera(oldCamera);

            // set target texture to mirror
            target.setSprite(new Sprite(writableImage));
        });
    }

    public double getNearClip() {
        return this.nearClip;
    }

    public double getFarClip() {
        return this.farClip;
    }

    public TransformSet getPosition() {
        return this.position;
    }

    public TransformSet getRotation() {
        return this.rotation;
    }

    @SerializationSetter
    public void setTarget(String identifier) {
        target = (Base3DShape) getObjectByName(identifier).getComponentByClass(Base3DShape.class);
        if(target == null) {
            Logger.log(1, "Not found texture target: " + identifier);
        }
    }

    public void setNearClip(double nearClip) {
        this.nearClip = nearClip;
    }

    @SerializationSetter
    public void setNearClip(String nearClip) {
        setNearClip(Double.parseDouble(nearClip));
    }

    public void setFarClip(double farClip) {
        this.farClip = farClip;
    }

    @SerializationSetter
    public void setFarClip(String farClip) {
        setFarClip(Double.parseDouble(farClip));
    }

    public void setPosition(TransformSet position) {
        this.position = position;
    }

    @SerializationSetter
    public void setPosition(String position) {
        setPosition(new TransformSet().deserialize(position));
    }

    public void setRotation(TransformSet rotation) {
        this.rotation = rotation;
    }

    @SerializationSetter
    public void setRotation(String rotation) {
        setRotation(new TransformSet().deserialize(rotation));
    }

    public void setDivider(int divider) {
        this.divider = divider;
    }

    @SerializationSetter
    public void setDivider(String divider) {
        setDivider(Integer.parseInt(divider));
    }

}
