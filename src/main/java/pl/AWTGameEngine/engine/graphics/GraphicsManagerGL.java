package pl.AWTGameEngine.engine.graphics;

import com.jogamp.opengl.GL2;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.objects.TransformSet;

import java.util.Arrays;
import java.util.HashMap;

public class GraphicsManagerGL extends GraphicsManager3D {

    private final PanelGL panelGL;
    private final HashMap<String, RenderOptions> renderables = new HashMap<>();

    private float angle;

    public GraphicsManagerGL(PanelGL panelGL) {
        this.panelGL = panelGL;
    }

    public void drawScene(GL2 gl) {
        for(RenderOptions options : renderables.values()) {
            gl.glTranslatef(options.getPosition().getFloatable().getX(), options.getPosition().getFloatable().getY(), options.getPosition().getFloatable().getZ());
            //gl.glRotatef(options.getRotation().getFloatable().getX(), options.getRotation().getFloatable().getY(), options.getRotation().getFloatable().getZ(), 0.0f);
            gl.glRotatef(angle, 1.0f, 1.0f, 0.0f); // ← obrót
            gl.glScalef(options.getSize().getFloatable().getX(), options.getSize().getFloatable().getY(), options.getSize().getFloatable().getZ());
            drawBox(gl);
            angle += 0.5f;
        }
    }

    private void drawBox(GL2 gl) {
        gl.glBegin(GL2.GL_QUADS);

        // Front
        gl.glColor3f(1, 0, 0);
        gl.glVertex3f(-1, -1,  1);
        gl.glVertex3f( 1, -1,  1);
        gl.glVertex3f( 1,  1,  1);
        gl.glVertex3f(-1,  1,  1);

        // Back
        gl.glColor3f(0, 1, 0);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f(-1,  1, -1);
        gl.glVertex3f( 1,  1, -1);
        gl.glVertex3f( 1, -1, -1);

        // Left
        gl.glColor3f(0, 0, 1);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f(-1, -1,  1);
        gl.glVertex3f(-1,  1,  1);
        gl.glVertex3f(-1,  1, -1);

        // Right
        gl.glColor3f(1, 1, 0);
        gl.glVertex3f(1, -1, -1);
        gl.glVertex3f(1,  1, -1);
        gl.glVertex3f(1,  1,  1);
        gl.glVertex3f(1, -1,  1);

        // Top
        gl.glColor3f(1, 0, 1);
        gl.glVertex3f(-1, 1, -1);
        gl.glVertex3f(-1, 1,  1);
        gl.glVertex3f( 1, 1,  1);
        gl.glVertex3f( 1, 1, -1);

        // Bottom
        gl.glColor3f(0, 1, 1);
        gl.glVertex3f(-1, -1, -1);
        gl.glVertex3f( 1, -1, -1);
        gl.glVertex3f( 1, -1,  1);
        gl.glVertex3f(-1, -1,  1);

        gl.glEnd();
    }

    @Override
    public void createBox(RenderOptions options) {
        if(renderables.containsKey(options.getIdentifier())) {
            return;
        }
        renderables.put(options.getIdentifier(), options);
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
        renderables.get(identifier).setPosition(position);
    }

    @Override
    public void updateSize(String identifier, ShapeType shape, TransformSet scale) {
        renderables.get(identifier).setSize(scale);
    }

    @Override
    public void updateRotation(String identifier, ShapeType shape, TransformSet rotation) {
        renderables.get(identifier).setRotation(rotation);
    }

    @Override
    public void updateSprite(String identifier, ShapeType shape, Sprite sprite) {
        renderables.get(identifier).setSprite(sprite);
    }

    @Override
    public void updateColor(String identifier, ShapeType shape, ColorObject color) {
        renderables.get(identifier).setColor(color);
    }

}