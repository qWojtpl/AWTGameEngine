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

public class GraphicsManagerFX extends GraphicsManager3D {

    private final Panel3D panel;
    protected final HashMap<String, Box> boxes = new HashMap<>();
    protected final HashMap<String, Sphere> spheres = new HashMap<>();
    protected final HashMap<String, Cylinder> cylinders = new HashMap<>();
    protected final HashMap<String, Group> models = new HashMap<>();

    private final ObjImporter importer = new ObjImporter();

    public GraphicsManagerFX(Panel3D panel) {
        this.panel = panel;
    }

    @Override
    public void createBox(RenderOptions options) {
        Platform.runLater(() -> {
            Box box = getBox(options.getIdentifier());
            if(box == null) {
                box = new Box();
                box.setMaterial(new PhongMaterial() {{
                    setDiffuseColor(Color.WHITE);
                }});
                if(options.isFrontCullFace()) {
                    box.setCullFace(CullFace.FRONT);
                }
                boxes.put(options.getIdentifier(), box);
                panel.getRootGroup().getChildren().add(box);
            }
            updatePosition(options.getIdentifier(), ShapeType.BOX, options.getPosition());
            updateSize(options.getIdentifier(), ShapeType.BOX, options.getSize());
            updateRotation(options.getIdentifier(), ShapeType.BOX, options.getRotation());
            updateSprite(options.getIdentifier(), ShapeType.BOX, options.getSprite());
            updateColor(options.getIdentifier(), ShapeType.BOX, options.getColor());
        });
    }

    @Override
    public void removeBox(String identifier) {
        Platform.runLater(() -> {
            Box box = getBox(identifier);
            if(box != null) {
                boxes.remove(identifier);
                panel.getRootGroup().getChildren().remove(box);
            }
        });
    }

    @Override
    public void createSphere(RenderOptions options) {
        Platform.runLater(() -> {
            Sphere sphere = getSphere(options.getIdentifier());
            if(sphere == null) {
                sphere = new Sphere();
                sphere.setMaterial(new PhongMaterial() {{
                    setDiffuseColor(Color.WHITE);
                }});
                if(options.isFrontCullFace()) {
                    sphere.setCullFace(CullFace.FRONT);
                }
                spheres.put(options.getIdentifier(), sphere);
                panel.getRootGroup().getChildren().add(sphere);
            }
            updatePosition(options.getIdentifier(), ShapeType.SPHERE, options.getPosition());
            updateSize(options.getIdentifier(), ShapeType.SPHERE, options.getSize());
            updateRotation(options.getIdentifier(), ShapeType.SPHERE, options.getRotation());
            updateSprite(options.getIdentifier(), ShapeType.SPHERE, options.getSprite());
            updateColor(options.getIdentifier(), ShapeType.SPHERE, options.getColor());
        });
    }

    @Override
    public void removeSphere(String identifier) {
        Platform.runLater(() -> {
            Sphere sphere = getSphere(identifier);
            if(sphere != null) {
                spheres.remove(identifier);
                panel.getRootGroup().getChildren().remove(sphere);
            }
        });
    }

    @Override
    public void createCylinder(RenderOptions options) {
        Platform.runLater(() -> {
            Cylinder cylinder = getCylinder(options.getIdentifier());
            if(cylinder == null) {
                cylinder = new Cylinder();
                cylinder.setMaterial(new PhongMaterial() {{
                    setDiffuseColor(Color.WHITE);
                }});
                if(options.isFrontCullFace()) {
                    cylinder.setCullFace(CullFace.FRONT);
                }
                cylinders.put(options.getIdentifier(), cylinder);
                panel.getRootGroup().getChildren().add(cylinder);
            }
            updatePosition(options.getIdentifier(), ShapeType.CYLINDER, options.getPosition());
            updateSize(options.getIdentifier(), ShapeType.CYLINDER, options.getSize());
            updateRotation(options.getIdentifier(), ShapeType.CYLINDER, options.getRotation());
            updateSprite(options.getIdentifier(), ShapeType.CYLINDER, options.getSprite());
            updateColor(options.getIdentifier(), ShapeType.CYLINDER, options.getColor());
        });
    }

    @Override
    public void removeCylinder(String identifier) {
        Platform.runLater(() -> {
            Cylinder cylinder = getCylinder(identifier);
            if(cylinder != null) {
                cylinders.remove(identifier);
                panel.getRootGroup().getChildren().remove(cylinder);
            }
        });
    }

    @Override
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
                updatePosition(options.getIdentifier(), ShapeType.MODEL, options.getPosition());
                updateSize(options.getIdentifier(), ShapeType.MODEL, options.getSize());
                updateRotation(options.getIdentifier(), ShapeType.MODEL, options.getRotation());
                updateSprite(options.getIdentifier(), ShapeType.MODEL, options.getSprite());
                updateColor(options.getIdentifier(), ShapeType.MODEL, options.getColor());
            } catch(IOException e) {
                Logger.log("Cannot create custom model", e);
            }
        });
    }

    @Override
    public void removeCustomModel(String identifier) {
        Platform.runLater(() -> {
            Group model = getCustomModel(identifier);
            if(model != null) {
                models.remove(identifier);
                panel.getRootGroup().getChildren().remove(model);
            }
        });
    }

    @Override
    public void updatePosition(String identifier, ShapeType shape, TransformSet position) {
        if(identifier == null || shape == null || position == null) {
            return;
        }
        Node node = getNode(identifier, shape);
        if(node == null) {
            return;
        }
        Platform.runLater(() -> {
            node.setTranslateX(panel.getCamera().parsePlainValue(position.getX()));
            node.setTranslateY(-panel.getCamera().parsePlainValue(position.getY()));
            node.setTranslateZ(panel.getCamera().parsePlainValue(position.getZ()));
        });
    }

    @Override
    public void updateSize(String identifier, ShapeType shape, TransformSet scale) {
        if(identifier == null || shape == null || scale == null) {
            return;
        }
        Node node = getNode(identifier, shape);
        if(node == null) {
            return;
        }
        Platform.runLater(() -> {
            node.setScaleX(panel.getCamera().parsePlainValue(scale.getX()));
            node.setScaleY(panel.getCamera().parsePlainValue(scale.getY()));
            node.setScaleZ(panel.getCamera().parsePlainValue(scale.getZ()));
        });
    }

    @Override
    public void updateRotation(String identifier, ShapeType shape, TransformSet rotation) {
        if(identifier == null || shape == null || rotation == null) {
            return;
        }
        Node node = getNode(identifier, shape);
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

    @Override
    public void updateSprite(String identifier, ShapeType shape, Sprite sprite) {
        if(identifier == null || shape == null || sprite == null) {
            return;
        }
        Node node = getNode(identifier, shape);
        if(node == null) {
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


    @Override
    public void updateColor(String identifier, ShapeType shape, ColorObject color) {
        if(identifier == null || shape == null || color == null) {
            return;
        }
        Node node = getNode(identifier, shape);
        if(node == null) {
            return;
        }
        Platform.runLater(() -> {
            ((Shape3D) node).setMaterial(new PhongMaterial() {{
                setDiffuseColor(color.getFxColor());
            }});
        });
    }

    private Node getNode(String identifier, ShapeType shape) {
        switch(shape) {
            case BOX -> {
                return getBox(identifier);
            }
            case CYLINDER -> {
                return getCylinder(identifier);
            }
            case SPHERE -> {
                return getSphere(identifier);
            }
            case MODEL -> {
                return getCustomModel(identifier);
            }
        }
        return null;
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