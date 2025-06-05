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
import pl.AWTGameEngine.objects.ColorObject;
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

    public void createBox(RenderOptions options) {
        Platform.runLater(() -> {
            Box box = getBox(options.getIdentifier());
            if(box == null) {
                box = new Box();
                box.setMaterial(new PhongMaterial() {{
                    setDiffuseColor(Color.WHITE);
                }});
                boxes.put(options.getIdentifier(), box);
                panel.getRootGroup().getChildren().add(box);
            }
            updatePosition(box, options.getPosition());
            updateSize(box, options.getSize());
            updateRotation(box, options.getRotation());
            updateSprite(box, options.getSprite());
            updateColor(box, options.getColor());
        });
    }

    public void removeBox(String identifier) {
        Platform.runLater(() -> {
            Box box = getBox(identifier);
            if(box != null) {
                boxes.remove(identifier);
                panel.getRootGroup().getChildren().remove(box);
            }
        });
    }

    public void createSphere(RenderOptions options) {
        Platform.runLater(() -> {
            Sphere sphere = getSphere(options.getIdentifier());
            if(sphere == null) {
                sphere = new Sphere();
                sphere.setMaterial(new PhongMaterial() {{
                    setDiffuseColor(Color.WHITE);
                }});
                spheres.put(options.getIdentifier(), sphere);
                panel.getRootGroup().getChildren().add(sphere);
            }
            updatePosition(sphere, options.getPosition());
            updateSize(sphere, options.getSize());
            updateRotation(sphere, options.getRotation());
            updateSprite(sphere, options.getSprite());
            updateColor(sphere, options.getColor());
        });
    }

    public void removeSphere(String identifier) {
        Platform.runLater(() -> {
            Sphere sphere = getSphere(identifier);
            if(sphere != null) {
                spheres.remove(identifier);
                panel.getRootGroup().getChildren().remove(sphere);
            }
        });
    }

    public void createCylinder(RenderOptions options) {
        Platform.runLater(() -> {
            Cylinder cylinder = getCylinder(options.getIdentifier());
            if(cylinder == null) {
                cylinder = new Cylinder();
                cylinder.setMaterial(new PhongMaterial() {{
                    setDiffuseColor(Color.WHITE);
                }});
                cylinders.put(options.getIdentifier(), cylinder);
                panel.getRootGroup().getChildren().add(cylinder);
            }
            updatePosition(cylinder, options.getPosition());
            updateSize(cylinder, options.getSize());
            updateRotation(cylinder, options.getRotation());
            updateSprite(cylinder, options.getSprite());
            updateColor(cylinder, options.getColor());
        });
    }

    public void removeCylinder(String identifier) {
        Platform.runLater(() -> {
            Cylinder cylinder = getCylinder(identifier);
            if(cylinder != null) {
                cylinders.remove(identifier);
                panel.getRootGroup().getChildren().remove(cylinder);
            }
        });
    }

    public void createCustomModel(RenderOptions options, String modelPath) {
        Platform.runLater(() -> {
            try {
                Group model = getCustomModel(options.getIdentifier());
                if(model == null) {
                    model = new Group(importer.load(new File(modelPath).toURI().toURL()).getMeshViews());
//                cylinder.setMaterial(new PhongMaterial() {{
//                    setDiffuseColor(Color.WHITE);
//                }});
                    models.put(options.getIdentifier(), model);
                    panel.getRootGroup().getChildren().add(model);
                }
                updatePosition(model, options.getPosition());
                updateSize(model, options.getSize());
                updateRotation(model, options.getRotation());
                updateSprite(model, options.getSprite());
                updateColor(model, options.getColor());
            } catch(IOException e) {
                Logger.log("Cannot create custom model", e);
            }
        });
    }

    public void removeCustomModel(String identifier) {
        Platform.runLater(() -> {
            Group model = getCustomModel(identifier);
            if(model != null) {
                models.remove(identifier);
                panel.getRootGroup().getChildren().remove(model);
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

    public void updateColor(Node node, ColorObject color) {
        if(node == null || color == null) {
            return;
        }
        Platform.runLater(() -> {
            if(node instanceof Shape3D) {
                ((Shape3D) node).setMaterial(new PhongMaterial() {{
                    setDiffuseColor(color.getFxColor());
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

    public static class RenderOptions {

        private final String identifier;
        private TransformSet position;
        private TransformSet size;
        private TransformSet rotation;
        private Sprite sprite;
        private ColorObject color;

        public RenderOptions(String identifier) {
            this.identifier = identifier;
        }

        public RenderOptions(String identifier, TransformSet position, TransformSet size, TransformSet rotation, Sprite sprite, ColorObject color) {
            this.identifier = identifier;
            this.position = position;
            this.size = size;
            this.rotation = rotation;
            this.sprite = sprite;
            this.color = color;
        }

        public String getIdentifier() {
            return identifier;
        }

        public TransformSet getPosition() {
            return position;
        }

        public TransformSet getSize() {
            return size;
        }

        public TransformSet getRotation() {
            return rotation;
        }

        public Sprite getSprite() {
            return sprite;
        }

        public ColorObject getColor() {
            return color;
        }

        public void setPosition(TransformSet position) {
            this.position = position;
        }

        public void setSize(TransformSet size) {
            this.size = size;
        }

        public void setRotation(TransformSet rotation) {
            this.rotation = rotation;
        }

        public void setSprite(Sprite sprite) {
            this.sprite = sprite;
        }

        public void setColor(ColorObject color) {
            this.color = color;
        }


    }

}
