package pl.AWTGameEngine.engine.graphics;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.objects.TransformSet;

import java.util.HashMap;

public class GraphicsManager3D {

    private final Panel3D panel;
    private final HashMap<String, Box> boxes = new HashMap<>();
    private final HashMap<String, Sphere> spheres = new HashMap<>();
    private final HashMap<String, Cylinder> cylinders = new HashMap<>();

    public GraphicsManager3D(Panel3D panel) {
        this.panel = panel;
    }

    public void createBox(String identifier, TransformSet position, TransformSet size, TransformSet rotation, Sprite sprite) {
        Platform.runLater(() -> {
            Box box = boxes.getOrDefault(identifier, null);
            if(box == null) {
                box = new Box();
                box.setMaterial(new PhongMaterial() {{
                    setDiffuseColor(Color.WHITE);
                }});
                boxes.put(identifier, box);
                panel.getRootGroup().getChildren().add(box);
            }
            updatePosition(box, position);
            updateSize(box, size);
            updateRotation(box, rotation);
            updateSprite(box, sprite);
        });
    }

    public void createSphere(String identifier, TransformSet position, TransformSet size, TransformSet rotation, Sprite sprite) {
        Platform.runLater(() -> {
            Sphere sphere = spheres.getOrDefault(identifier, null);
            if(sphere == null) {
                sphere = new Sphere();
                sphere.setMaterial(new PhongMaterial() {{
                    setDiffuseColor(Color.WHITE);
                }});
                spheres.put(identifier, sphere);
                panel.getRootGroup().getChildren().add(sphere);
            }
            updatePosition(sphere, position);
            updateSize(sphere, size);
            updateRotation(sphere, rotation);
            updateSprite(sphere, sprite);
        });
    }

    public void createCylinder(String identifier, TransformSet position, TransformSet size, TransformSet rotation, Sprite sprite) {
        Platform.runLater(() -> {
            Cylinder cylinder = cylinders.getOrDefault(identifier, null);
            if(cylinder == null) {
                cylinder = new Cylinder();
                cylinder.setMaterial(new PhongMaterial() {{
                    setDiffuseColor(Color.WHITE);
                }});
                cylinders.put(identifier, cylinder);
                panel.getRootGroup().getChildren().add(cylinder);
            }
            updatePosition(cylinder, position);
            updateSize(cylinder, size);
            updateRotation(cylinder, rotation);
            updateSprite(cylinder, sprite);
        });
    }

    public void updatePosition(Shape3D shape, TransformSet position) {
        if(shape == null) {
            return;
        }
        Platform.runLater(() -> {
            shape.setTranslateX(panel.getCamera().parsePlainValue(position.getX()));
            shape.setTranslateY(-panel.getCamera().parsePlainValue(position.getY()));
            shape.setTranslateZ(panel.getCamera().parsePlainValue(position.getZ()));
        });
    }

    public void updateSize(Shape3D shape, TransformSet scale) {
        if(shape == null) {
            return;
        }
        Platform.runLater(() -> {
            shape.setScaleX(panel.getCamera().parsePlainValue(scale.getX()));
            shape.setScaleY(panel.getCamera().parsePlainValue(scale.getY()));
            shape.setScaleZ(panel.getCamera().parsePlainValue(scale.getZ()));
        });
    }

    public void updateRotation(Shape3D shape, TransformSet rotation) {
        if(shape == null) {
            return;
        }
        Platform.runLater(() -> {
            shape.setRotationAxis(Rotate.X_AXIS);
            shape.setRotate(rotation.getX());
            shape.setRotationAxis(Rotate.Y_AXIS);
            shape.setRotate(rotation.getY());
            shape.setRotationAxis(Rotate.Z_AXIS);
            shape.setRotate(rotation.getZ());
        });
    }

    public void updateSprite(Shape3D shape, Sprite sprite) {
        if(shape == null || sprite == null) {
            return;
        }
        Platform.runLater(() -> {
            shape.setMaterial(new PhongMaterial() {{
                setDiffuseMap(SwingFXUtils.toFXImage(sprite.getImage(), null));
            }});
        });
    }

    public Box getBox(String identifier) {
        return boxes.getOrDefault(identifier, null);
    }

    public Sphere getSphere(String identifier) {
        return spheres.getOrDefault(identifier, null);
    }

    public Cylinder getCylinder(String identifier) {
        return cylinders.getOrDefault(identifier, null);
    }

}
