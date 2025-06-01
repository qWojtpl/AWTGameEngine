package pl.AWTGameEngine.engine.graphics;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import org.fxyz3d.importers.obj.ObjImporter;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.objects.TransformSet;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

public class GraphicsManager3D {

    private final Panel3D panel;
    private final HashMap<String, Box> boxes = new HashMap<>();
    private final HashMap<String, Sphere> spheres = new HashMap<>();
    private final HashMap<String, Cylinder> cylinders = new HashMap<>();
    private final HashMap<String, Group> models = new HashMap<>();

    private final ObjImporter importer = new ObjImporter();

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

    public void createCustomModel(String identifier, TransformSet position, TransformSet size, TransformSet rotation, Sprite sprite, String modelPath) {
        Platform.runLater(() -> {
            try {
                Group model = models.getOrDefault(identifier, null);
                if(model == null) {
                    model = new Group(importer.load(new File(modelPath).toURI().toURL()).getMeshViews());
//                cylinder.setMaterial(new PhongMaterial() {{
//                    setDiffuseColor(Color.WHITE);
//                }});
                    models.put(identifier, model);
                    panel.getRootGroup().getChildren().add(model);
                }
                updatePosition(model, position);
                updateSize(model, size);
                updateRotation(model, rotation);
                updateSprite(model, sprite);
            } catch(IOException e) {
                Logger.log("Cannot create custom model", e);
            }
        });
    }

    public void updatePosition(Node node, TransformSet position) {
        if(node == null) {
            return;
        }
        Platform.runLater(() -> {
            node.setTranslateX(panel.getCamera().parsePlainValue(position.getX()));
            node.setTranslateY(-panel.getCamera().parsePlainValue(position.getY()));
            node.setTranslateZ(panel.getCamera().parsePlainValue(position.getZ()));
        });
    }

    public void updateSize(Node node, TransformSet scale) {
        if(node == null) {
            return;
        }
        Platform.runLater(() -> {
            node.setScaleX(panel.getCamera().parsePlainValue(scale.getX()));
            node.setScaleY(panel.getCamera().parsePlainValue(scale.getY()));
            node.setScaleZ(panel.getCamera().parsePlainValue(scale.getZ()));
        });
    }

    public void updateRotation(Node node, TransformSet rotation) {
        if(node == null) {
            return;
        }
        Platform.runLater(() -> {
            node.setRotationAxis(Rotate.X_AXIS);
            node.setRotate(rotation.getX());
            node.setRotationAxis(Rotate.Y_AXIS);
            node.setRotate(rotation.getY());
            node.setRotationAxis(Rotate.Z_AXIS);
            node.setRotate(rotation.getZ());
        });
    }

    public void updateSprite(Node node, Sprite sprite) {
        if(node == null || sprite == null) {
            return;
        }
        Platform.runLater(() -> {
            if(node instanceof Shape3D) {
                ((Shape3D) node).setMaterial(new PhongMaterial() {{
                    setDiffuseMap(SwingFXUtils.toFXImage(sprite.getImage(), null));
                }});
            }
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

    public Group getCustomModel(String identifier) {
        return models.getOrDefault(identifier, null);
    }

    public Collection<Box> getBoxes() {
        return boxes.values();
    }

}
