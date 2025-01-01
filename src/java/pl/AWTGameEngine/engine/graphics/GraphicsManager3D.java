package pl.AWTGameEngine.engine.graphics;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.objects.TransformSet;

import java.util.HashMap;

public class GraphicsManager3D {

    private final Panel3D panel;
    private final HashMap<String, Box> boxes = new HashMap<>();

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
                    setSpecularColor(Color.WHITE);
                    setSpecularPower(50);
                }});
                boxes.put(identifier, box);
                panel.getRootGroup().getChildren().add(box);
            }
            box.setScaleX(panel.getCamera().parsePlainValue(scale.getX()));
            box.setScaleY(panel.getCamera().parsePlainValue(scale.getY()));
            box.setScaleZ(panel.getCamera().parsePlainValue(scale.getZ()));
            box.setTranslateX(panel.getCamera().parsePlainValue(position.getX()));
            box.setTranslateY(panel.getCamera().parsePlainValue(position.getY()));
            box.setTranslateZ(panel.getCamera().parsePlainValue(position.getZ()));
            box.getTransforms().clear();
            box.getTransforms().add(new Rotate(rotation.getX(), Rotate.X_AXIS));
            box.getTransforms().add(new Rotate(rotation.getY(), Rotate.Y_AXIS));
            box.getTransforms().add(new Rotate(rotation.getZ(), Rotate.Z_AXIS));
        });
    }

}
