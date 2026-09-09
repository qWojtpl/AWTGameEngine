package pl.AWTGameEngine.components;

import javafx.scene.image.WritableImage;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Shaders;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.helpers.FXHelper;
import pl.AWTGameEngine.engine.panels.Panel3D;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.engine.panels.WebPanel;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.Sprite;
import pl.AWTGameEngine.objects.render.shaders.GUIShader;
import pl.AWTGameEngine.objects.transform.Vector4;
import pl.AWTGameEngine.objects.transform.Vector3;

import java.awt.image.BufferedImage;

@WebComponent
public class GUIRenderer extends ObjectComponent {

    private RenderOptions3D renderOptions3D;
    private GraphicsManager3D graphicsManager;
    private volatile boolean finished = true;
    private final Sprite sprite = new Sprite("GUIRendererSprite", new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
    private WritableImage image = new WritableImage(getWindow().getBaseWidth(), getWindow().getBaseHeight());

    public GUIRenderer(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        Panel3D panel = (Panel3D) getWindow().getCurrentScene().getPanel();
        graphicsManager = panel.getGraphicsManager3D();
        renderOptions3D = new RenderOptions3D(getObject().getIdentifier() + "-" + "GUIRenderer" + "-" + getScene().getName())
                .setPosition(new Vector3())
                .setSize(new Vector3())
                .setQuaternionRotation(new Vector4())
                .setShader(Shaders.of(getWindow(), GUIShader.class))
                .setShapePath("models/plane.obj")
                .setSprite(sprite);
        graphicsManager.createRenderable(renderOptions3D);
    }

    @Override
    public void onRemoveComponent() {
        graphicsManager.removeRenderable(renderOptions3D.getIdentifier());
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
                graphicsManager.updateTexture(sprite);
            } finally {
                finished = true;
            }
            return null;
        });
    }

}
