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

    public void renderBox(String identifier, TransformSet position, TransformSet scale, TransformSet rotation, Sprite sprite) {
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
            renderShape3D(box, position, scale, rotation, sprite);
        });
    }

    public void renderSphere(String identifier, TransformSet position, TransformSet scale, TransformSet rotation, Sprite sprite) {
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
            renderShape3D(sphere, position, scale, rotation, sprite);
        });
    }

    public void renderCylinder(String identifier, TransformSet position, TransformSet scale, TransformSet rotation, Sprite sprite) {
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
           renderShape3D(cylinder, position, scale, rotation, sprite);
        });
    }

    private void renderShape3D(Shape3D shape, TransformSet position, TransformSet scale, TransformSet rotation, Sprite sprite) {
        if(sprite != null) {
            shape.setMaterial(new PhongMaterial() {{
                setDiffuseMap(SwingFXUtils.toFXImage(sprite.getImage(), null));
            }});
        }
        shape.setScaleX(panel.getCamera().parsePlainValue(scale.getX()));
        shape.setScaleY(panel.getCamera().parsePlainValue(scale.getY()));
        shape.setScaleZ(panel.getCamera().parsePlainValue(scale.getZ()));
        shape.setTranslateX(panel.getCamera().parsePlainValue(position.getX()));
        shape.setTranslateY(-panel.getCamera().parsePlainValue(position.getY()));
        shape.setTranslateZ(panel.getCamera().parsePlainValue(position.getZ()));
        shape.getTransforms().clear();
        shape.getTransforms().add(new Rotate(rotation.getX(), Rotate.X_AXIS));
        shape.getTransforms().add(new Rotate(rotation.getY(), Rotate.Y_AXIS));
        shape.getTransforms().add(new Rotate(rotation.getZ(), Rotate.Z_AXIS));
    }

}
