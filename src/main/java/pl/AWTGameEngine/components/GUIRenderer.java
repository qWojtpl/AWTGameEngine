package pl.AWTGameEngine.components;

import javafx.scene.image.WritableImage;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.helpers.FXHelper;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.Sprite;
import pl.AWTGameEngine.objects.transform.Vector4;
import pl.AWTGameEngine.objects.transform.Vector3;

import java.awt.image.BufferedImage;

@WebComponent
public class GUIRenderer extends ObjectComponent {

    private RenderOptions3D renderOptions3D;
    private GraphicsManagerGL graphicsManagerGL;
    private volatile boolean finished = true;
    private Sprite sprite = new Sprite("GUIRendererSprite", new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
    private WritableImage image = new WritableImage(1920, 1080);

    public GUIRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        PanelGL panelGL = (PanelGL) getWindow().getCurrentScene().getPanel();
        graphicsManagerGL = (GraphicsManagerGL) panelGL.getGraphicsManager3D();
        renderOptions3D = new RenderOptions3D(getObject().getIdentifier() + "-" + "GUIRenderer" + "-" + getScene().getName())
                .setPosition(new Vector3())
                .setSize(new Vector3())
                .setQuaternionRotation(new Vector4())
                .setShader("shaders/gui")
                .setShapePath("models/plane.obj")
                .setSprite(sprite);
        graphicsManagerGL.createRenderable(renderOptions3D);
    }

    @Override
    public void onRemoveComponent() {
        graphicsManagerGL.removeRenderable(renderOptions3D.getIdentifier());
    }

    @Override
    public void onGUIUpdate() {
        if(!finished) {
            return;
        }
        finished = false;
        FXHelper.synchronizedCall(() -> {
            try {
                image = ((WebPanel) getScene().getPanel()).getScene().snapshot(image);
                sprite.updateImage(image);
                graphicsManagerGL.updateTexture(sprite);
            } finally {
                finished = true;
            }
            return null;
        });
    }

}
