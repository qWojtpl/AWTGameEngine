package pl.AWTGameEngine.engine.graphics;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.TransformSet;

import java.util.HashMap;

public class GraphicsManager3D {

    private Panel3D panel;
    private HashMap<String, Box> boxes = new HashMap<>();

    public GraphicsManager3D(Panel3D panel) {
        this.panel = panel;
    }

    public void renderBox(String identifier, TransformSet position, TransformSet scale, TransformSet rotation) {
        Platform.runLater(() -> {
            Box box = boxes.getOrDefault(identifier, null);
            if(box == null) {
                box = new Box();
                box.setMaterial(new PhongMaterial() {{
                    setDiffuseMap(new Image("sprites/beaver.jpg"));
                }});
                boxes.put(identifier, box);
                panel.getRootGroup().getChildren().add(box);
            }
            box.setScaleX(scale.getX());
            box.setScaleY(scale.getY());
            box.setScaleZ(scale.getZ());
            box.setTranslateX(position.getX());
            box.setTranslateY(position.getY());
            box.setTranslateZ(position.getZ());
            box.getTransforms().clear();
            box.getTransforms().add(new Rotate(rotation.getX(), Rotate.X_AXIS));
            box.getTransforms().add(new Rotate(rotation.getY(), Rotate.Y_AXIS));
            box.getTransforms().add(new Rotate(rotation.getZ(), Rotate.Z_AXIS));
        });
    }

}
