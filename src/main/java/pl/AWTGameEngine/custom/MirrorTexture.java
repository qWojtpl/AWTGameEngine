package pl.AWTGameEngine.custom;

import javafx.application.Platform;
import javafx.scene.*;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Rotate;
import pl.AWTGameEngine.annotations.ComponentFX;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.components.base.Base3DShape;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.Renderable3D;
import pl.AWTGameEngine.engine.panels.PanelFX;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.Sprite;

@ComponentFX
public class MirrorTexture extends ObjectComponent implements Renderable3D {

    private Base3DShape target;
    private PanelFX panel;
    private int counter = 0;
    private int divider = 10;
    private double nearClip = 0.1;
    private double farClip = 1000;

    public MirrorTexture(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        if(target == null) {
            setTarget(getObject().getIdentifier());
        }
        panel = (PanelFX) getPanel();
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

            cameraPitch.setTranslateX(getObject().getPosition().getX());
            cameraPitch.setTranslateY(-getObject().getPosition().getY());
            cameraPitch.setTranslateZ(getObject().getPosition().getZ());

            cameraPitch.setRotationAxis(Rotate.X_AXIS);
            cameraPitch.setRotate(getObject().getRotation().getX());
            cameraYaw.setRotationAxis(Rotate.Y_AXIS);
            cameraYaw.setRotate(getObject().getRotation().getY());
            newCamera.setRotationAxis(Rotate.Z_AXIS);
            newCamera.setRotate(getObject().getRotation().getZ());

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

    @SerializationSetter
    public void setTarget(String identifier) {
        target = (Base3DShape) getObjectByName(identifier).getComponentByClass(Base3DShape.class);
        if(target == null) {
            Logger.error("Not found texture target: " + identifier);
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

    public void setDivider(int divider) {
        this.divider = divider;
    }

    @SerializationSetter
    public void setDivider(String divider) {
        setDivider(Integer.parseInt(divider));
    }

}
