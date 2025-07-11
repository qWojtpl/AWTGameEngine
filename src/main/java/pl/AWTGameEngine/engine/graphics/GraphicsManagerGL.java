package pl.AWTGameEngine.engine.graphics;

import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.objects.TransformSet;

public class GraphicsManagerGL extends GraphicsManager3D {

    private final PanelGL panelGL;

    public GraphicsManagerGL(PanelGL panelGL) {
        this.panelGL = panelGL;
    }

    @Override
    public void createBox(RenderOptions options) {

    }

    @Override
    public void removeBox(String identifier) {

    }

    @Override
    public void createSphere(RenderOptions options) {

    }

    @Override
    public void removeSphere(String identifier) {

    }

    @Override
    public void createCylinder(RenderOptions options) {

    }

    @Override
    public void removeCylinder(String identifier) {

    }

    @Override
    public void createCustomModel(RenderOptions options, String modelPath) {

    }

    @Override
    public void removeCustomModel(String identifier) {

    }

    @Override
    public void updatePosition(String identifier, ShapeType shape, TransformSet position) {

    }

    @Override
    public void updateSize(String identifier, ShapeType shape, TransformSet scale) {

    }

    @Override
    public void updateRotation(String identifier, ShapeType shape, TransformSet rotation) {

    }

    @Override
    public void updateSprite(String identifier, ShapeType shape, Sprite sprite) {

    }

    @Override
    public void updateColor(String identifier, ShapeType shape, ColorObject color) {

    }


}