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
import pl.AWTGameEngine.engine.panels.PanelFX;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.QuaternionTransformSet;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.objects.TransformSet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class GraphicsManagerFX extends GraphicsManager3D {

    private final PanelFX panel;
    private final HashMap<String, Renderable> renderables = new HashMap<>();

    private final ObjImporter importer = new ObjImporter();

    public GraphicsManagerFX(PanelFX panel) {
        this.panel = panel;
    }

    public static class Renderable {

        private final RenderOptions renderOptions;
        private final Group group;

        public Renderable(RenderOptions renderOptions, Group group) {
            this.renderOptions = renderOptions;
            this.group = group;
        }

        public RenderOptions getRenderOptions() {
            return this.renderOptions;
        }

        public Group getGroup() {
            return this.group;
        }

        public Node getNode() {
            return this.group.getChildren().get(0);
        }

    }

    @Override
    public void createBox(RenderOptions options) {
        Platform.runLater(() -> {
            Renderable renderable = renderables.getOrDefault(options.getIdentifier(), null);
            Box box = null;
            if(renderable != null) {
                box = (Box) renderable.getNode();
            }
            if(box == null) {
                box = new Box();
                box.setMaterial(new PhongMaterial() {{
                    setDiffuseColor(Color.WHITE);
                }});
                if(options.isFrontCullFace()) {
                    box.setCullFace(CullFace.FRONT);
                }
                Group g = new Group(box);
                renderables.put(options.getIdentifier(), new Renderable(options, g));
                panel.getRootGroup().getChildren().add(g);
            }
            updatePosition(options.getIdentifier(), ShapeType.BOX, options.getPosition());
            updateSize(options.getIdentifier(), ShapeType.BOX, options.getSize());
            updateRotation(options.getIdentifier(), ShapeType.BOX, options.getRotation(), options.getQuaternionRotation());
            updateSprite(options.getIdentifier(), ShapeType.BOX, options.getSprite());
            updateColor(options.getIdentifier(), ShapeType.BOX, options.getColor());
        });
    }

    @Override
    public void removeBox(String identifier) {
        Platform.runLater(() -> {
            Renderable renderable = renderables.getOrDefault(identifier, null);
            Box box = null;
            if(renderable != null) {
                box = (Box) renderable.getNode();
            }
            if(box != null) {
                renderables.remove(identifier);
                panel.getRootGroup().getChildren().remove(box);
            }
        });
    }

    @Override
    public void createSphere(RenderOptions options) {
        Platform.runLater(() -> {
            Renderable renderable = renderables.getOrDefault(options.getIdentifier(), null);
            Sphere sphere = null;
            if(renderable != null) {
                sphere = (Sphere) renderable.getNode();
            }
            if(sphere == null) {
                sphere = new Sphere();
                sphere.setMaterial(new PhongMaterial() {{
                    setDiffuseColor(Color.WHITE);
                }});
                if(options.isFrontCullFace()) {
                    sphere.setCullFace(CullFace.FRONT);
                }
                Group g = new Group(sphere);
                renderables.put(options.getIdentifier(), new Renderable(options, g));
                panel.getRootGroup().getChildren().add(g);
            }
            updatePosition(options.getIdentifier(), ShapeType.SPHERE, options.getPosition());
            updateSize(options.getIdentifier(), ShapeType.SPHERE, options.getSize());
            updateRotation(options.getIdentifier(), ShapeType.SPHERE, options.getRotation(), options.getQuaternionRotation());
            updateSprite(options.getIdentifier(), ShapeType.SPHERE, options.getSprite());
            updateColor(options.getIdentifier(), ShapeType.SPHERE, options.getColor());
        });
    }

    @Override
    public void removeSphere(String identifier) {
        Platform.runLater(() -> {
            Renderable renderable = renderables.getOrDefault(identifier, null);
            Sphere sphere = null;
            if(renderable != null) {
                sphere = (Sphere) renderable.getNode();
            }
            if(sphere != null) {
                renderables.remove(identifier);
                panel.getRootGroup().getChildren().remove(sphere);
            }
        });
    }

    @Override
    public void createCylinder(RenderOptions options) {
        Platform.runLater(() -> {
            Renderable renderable = renderables.getOrDefault(options.getIdentifier(), null);
            Cylinder cylinder = null;
            if(renderable != null) {
                cylinder = (Cylinder) renderable.getNode();
            }
            if(cylinder == null) {
                cylinder = new Cylinder();
                cylinder.setMaterial(new PhongMaterial() {{
                    setDiffuseColor(Color.WHITE);
                }});
                if(options.isFrontCullFace()) {
                    cylinder.setCullFace(CullFace.FRONT);
                }
                Group g = new Group(cylinder);
                renderables.put(options.getIdentifier(), new Renderable(options, g));
                panel.getRootGroup().getChildren().add(g);
            }
            updatePosition(options.getIdentifier(), ShapeType.CYLINDER, options.getPosition());
            updateSize(options.getIdentifier(), ShapeType.CYLINDER, options.getSize());
            updateRotation(options.getIdentifier(), ShapeType.CYLINDER, options.getRotation(), options.getQuaternionRotation());
            updateSprite(options.getIdentifier(), ShapeType.CYLINDER, options.getSprite());
            updateColor(options.getIdentifier(), ShapeType.CYLINDER, options.getColor());
        });
    }

    @Override
    public void removeCylinder(String identifier) {
        Platform.runLater(() -> {
            Renderable renderable = renderables.getOrDefault(identifier, null);
            Cylinder cylinder = null;
            if(renderable != null) {
                cylinder = (Cylinder) renderable.getNode();
            }
            if(cylinder != null) {
                renderables.remove(identifier);
                panel.getRootGroup().getChildren().remove(cylinder);
            }
        });
    }

    @Override
    public void createCustomModel(RenderOptions options, String modelPath) {
        Platform.runLater(() -> {
            try {
                Group model = renderables.getOrDefault(options.getIdentifier(), null).getGroup();
                if(model == null) {
                    model = new Group(importer.load(new File(modelPath).toURI().toURL()).getMeshViews());
//                cylinder.setMaterial(new PhongMaterial() {{
//                    setDiffuseColor(Color.WHITE);
//                }});
                    renderables.put(options.getIdentifier(), new Renderable(options, model));
                    panel.getRootGroup().getChildren().add(model);
                }
                updatePosition(options.getIdentifier(), ShapeType.MODEL, options.getPosition());
                updateSize(options.getIdentifier(), ShapeType.MODEL, options.getSize());
                updateRotation(options.getIdentifier(), ShapeType.MODEL, options.getRotation(), options.getQuaternionRotation());
                updateSprite(options.getIdentifier(), ShapeType.MODEL, options.getSprite());
                updateColor(options.getIdentifier(), ShapeType.MODEL, options.getColor());
            } catch(IOException e) {
                Logger.exception("Cannot create custom model", e);
            }
        });
    }

    @Override
    public void removeCustomModel(String identifier) {
        Platform.runLater(() -> {
            Renderable renderable = renderables.getOrDefault(identifier, null);
            Group model = null;
            if(renderable != null) {
                model = renderable.getGroup();
            }
            if(model != null) {
                renderables.remove(identifier);
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
    public void updateRotation(String identifier, ShapeType shape, TransformSet rotation, QuaternionTransformSet quaternionRotation) {
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
            case BOX, SPHERE, CYLINDER -> {
                Renderable renderable = renderables.getOrDefault(identifier, null);
                if(renderable != null) {
                    return renderable.getNode();
                }
                return null;
            }
            case MODEL -> {
                Renderable renderable = renderables.getOrDefault(identifier, null);
                if(renderable != null) {
                    return renderable.getGroup();
                }
                return null;
            }
        }
        return null;
    }

}