package pl.AWTGameEngine.components;

import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import pl.AWTGameEngine.annotations.ComponentFX;
import pl.AWTGameEngine.annotations.FromXML;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.panels.PanelFX;
import pl.AWTGameEngine.objects.GameObject;

@ComponentFX
@Unique
public class CameraFX extends ObjectComponent {

    private final PerspectiveCamera perspectiveCamera = new PerspectiveCamera(true);
    private final javafx.scene.Group cameraYaw = new Group();
    private final javafx.scene.Group cameraPitch = new Group();
    private PanelFX panelFX;
    private double nearClip = 0.1;
    private double farClip = 10000;
    private double cullingDistance = 20000;

    public CameraFX(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        this.panelFX = (PanelFX) getPanel();

        Platform.runLater(() -> {
            perspectiveCamera.setNearClip(nearClip);
            perspectiveCamera.setFarClip(farClip);
            cameraPitch.getChildren().add(perspectiveCamera);
            cameraYaw.getChildren().add(cameraPitch);
            panelFX.getRootGroup().getChildren().add(cameraYaw);

            panelFX.getFxScene().setCamera(perspectiveCamera);
        });
    }

    @Override
    public void onRemoveComponent() {
        Platform.runLater(() -> {
            panelFX.getRootGroup().getChildren().remove(cameraYaw);
            if(panelFX.getFxScene().getCamera().equals(perspectiveCamera)) {
                panelFX.getFxScene().setCamera(null);
            }
        });
    }

    @Override
    public boolean onUpdatePosition(double newX, double newY, double newZ) {
        Platform.runLater(() -> {
            cameraPitch.setTranslateX(newX);
            cameraPitch.setTranslateY(newY);
            cameraPitch.setTranslateZ(newZ);

            Point3D cameraPosition = new Point3D(newX, newY, newZ);

//            for(Box box : panel3D.getGraphicsManager3D().getBoxes()) {
//                Point3D boxPosition = new Point3D(box.getTranslateX(), box.getTranslateY(), box.getTranslateZ());
//                box.setVisible(cameraPosition.distance(boxPosition) < cullingDistance);
//            }
        });
        return true;
    }

    @Override
    public void onUpdateRotation() {
        Platform.runLater(() -> {
            cameraPitch.setRotationAxis(Rotate.X_AXIS);
            cameraPitch.setRotate(getObject().getRotation().getX());
            cameraYaw.setRotationAxis(Rotate.Y_AXIS);
            cameraYaw.setRotate(getObject().getRotation().getY());
            perspectiveCamera.setRotationAxis(Rotate.Z_AXIS);
            perspectiveCamera.setRotate(getObject().getRotation().getZ());
        });
    }

    public PerspectiveCamera getPerspectiveCamera() {
        return this.perspectiveCamera;
    }

    public javafx.scene.Group getCameraYaw() {
        return this.cameraYaw;
    }

    public javafx.scene.Group getCameraPitch() {
        return this.cameraPitch;
    }

    public double getNearClip() {
        return this.nearClip;
    }

    public double getFarClip() {
        return this.farClip;
    }

    public double getCullingDistance() {
        return this.cullingDistance;
    }

    public void setNearClip(double nearClip) {
        this.nearClip = nearClip;
    }

    @FromXML
    public void setNearClip(String nearClip) {
        setNearClip(Double.parseDouble(nearClip));
    }

    public void setFarClip(double farClip) {
        this.farClip = farClip;
    }

    @FromXML
    public void setFarClip(String farClip) {
        setFarClip(Double.parseDouble(farClip));
    }

    public void setCullingDistance(double cullingDistance) {
        this.cullingDistance = cullingDistance;
    }

    @FromXML
    public void setCullingDistance(String cullingDistance) {
        setCullingDistance(Double.parseDouble(cullingDistance));
    }

}
