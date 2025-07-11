package pl.AWTGameEngine.components;

import javafx.application.Platform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import pl.AWTGameEngine.annotations.Component3D;
import pl.AWTGameEngine.annotations.SerializationSetter;
import pl.AWTGameEngine.annotations.Unique;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.GameObject;

@Component3D
@Unique
public class Camera3D extends ObjectComponent {

    private final PerspectiveCamera perspectiveCamera = new PerspectiveCamera(true);
    private final javafx.scene.Group cameraYaw = new Group();
    private final javafx.scene.Group cameraPitch = new Group();
    private Panel3D panel3D;
    private double nearClip = 0.1;
    private double farClip = 10000;
    private double cullingDistance = 20000;

    public Camera3D(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        this.panel3D = (Panel3D) getPanel();

        Platform.runLater(() -> {
            perspectiveCamera.setNearClip(nearClip);
            perspectiveCamera.setFarClip(farClip);
            cameraPitch.getChildren().add(perspectiveCamera);
            cameraYaw.getChildren().add(cameraPitch);
            panel3D.getRootGroup().getChildren().add(cameraYaw);

            panel3D.getFxScene().setCamera(perspectiveCamera);
        });
    }

    @Override
    public void onRemoveComponent() {
        Platform.runLater(() -> {
            panel3D.getRootGroup().getChildren().remove(cameraYaw);
            if(panel3D.getFxScene().getCamera().equals(perspectiveCamera)) {
                panel3D.getFxScene().setCamera(null);
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
    public boolean onUpdateRotation(double newX, double newY, double newZ) {
        Platform.runLater(() -> {
            cameraPitch.setRotationAxis(Rotate.X_AXIS);
            cameraPitch.setRotate(newX);
            cameraYaw.setRotationAxis(Rotate.Y_AXIS);
            cameraYaw.setRotate(newY);
            perspectiveCamera.setRotationAxis(Rotate.Z_AXIS);
            perspectiveCamera.setRotate(newZ);
        });
        return true;
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

    public void setCullingDistance(double cullingDistance) {
        this.cullingDistance = cullingDistance;
    }

    @SerializationSetter
    public void setCullingDistance(String cullingDistance) {
        setCullingDistance(Double.parseDouble(cullingDistance));
    }

}
