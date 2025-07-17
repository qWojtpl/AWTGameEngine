package pl.AWTGameEngine.engine.graphics;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.texture.Texture;
import pl.AWTGameEngine.engine.helpers.RotationHelper;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.QuaternionTransformSet;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.objects.TransformSet;

import java.util.HashMap;

public class GraphicsManagerGL extends GraphicsManager3D {

    private final PanelGL panelGL;
    private final HashMap<String, RenderOptions> renderables = new HashMap<>();

    public GraphicsManagerGL(PanelGL panelGL) {
        this.panelGL = panelGL;
    }

    public void drawScene(GL2 gl) {
        for (RenderOptions options : renderables.values()) {
            gl.glPushMatrix();
            gl.glTranslated(options.getPosition().getX(), options.getPosition().getY(), options.getPosition().getZ());
            double[] axis = RotationHelper.quaternionToAxisAngle(
                    options.getQuaternionRotation().getX(),
                    options.getQuaternionRotation().getY(),
                    options.getQuaternionRotation().getZ(),
                    options.getQuaternionRotation().getW()
            );
            gl.glRotated(axis[0], axis[1], axis[2], axis[3]);
            gl.glScaled(options.getSize().getX(), options.getSize().getY(), options.getSize().getZ());
            if (ShapeType.BOX.equals(options.getShapeType())) {
                drawBox(gl, options);
            }
            gl.glPopMatrix();
        }
    }

    private void drawBox(GL2 gl, RenderOptions renderOptions) {

        if(renderOptions.getGlTexture() != null) {
            Texture texture = panelGL.getTexture(renderOptions.getGlTexture());
            texture.enable(gl);
            texture.bind(gl);
        }

        gl.glColor3f(1f, 1f, 1f);
        gl.glBegin(GL2.GL_QUADS);

        // Front
        gl.glTexCoord2f(0f, 1f); gl.glVertex3f(-1, -1,  1);
        gl.glTexCoord2f(1f, 1f); gl.glVertex3f( 1, -1,  1);
        gl.glTexCoord2f(1f, 0f); gl.glVertex3f( 1,  1,  1);
        gl.glTexCoord2f(0f, 0f); gl.glVertex3f(-1,  1,  1);

        // Back
        gl.glTexCoord2f(1f, 1f); gl.glVertex3f(-1, -1, -1);
        gl.glTexCoord2f(1f, 0f); gl.glVertex3f(-1,  1, -1);
        gl.glTexCoord2f(0f, 0f); gl.glVertex3f( 1,  1, -1);
        gl.glTexCoord2f(0f, 1f); gl.glVertex3f( 1, -1, -1);

        // Left
        gl.glTexCoord2f(1f, 1f); gl.glVertex3f(-1, -1, -1);
        gl.glTexCoord2f(0f, 1f); gl.glVertex3f(-1, -1,  1);
        gl.glTexCoord2f(0f, 0f); gl.glVertex3f(-1,  1,  1);
        gl.glTexCoord2f(1f, 0f); gl.glVertex3f(-1,  1, -1);

        // Right
        gl.glTexCoord2f(0f, 1f); gl.glVertex3f(1, -1, -1);
        gl.glTexCoord2f(1f, 1f); gl.glVertex3f(1,  1, -1);
        gl.glTexCoord2f(1f, 0f); gl.glVertex3f(1,  1,  1);
        gl.glTexCoord2f(0f, 0f); gl.glVertex3f(1, -1,  1);

        // Top
        gl.glTexCoord2f(0f, 0f); gl.glVertex3f(-1, 1, -1);
        gl.glTexCoord2f(0f, 1f); gl.glVertex3f(-1, 1,  1);
        gl.glTexCoord2f(1f, 1f); gl.glVertex3f( 1, 1,  1);
        gl.glTexCoord2f(1f, 0f); gl.glVertex3f( 1, 1, -1);

        // Bottom
        gl.glTexCoord2f(1f, 0f); gl.glVertex3f(-1, -1, -1);
        gl.glTexCoord2f(0f, 0f); gl.glVertex3f( 1, -1, -1);
        gl.glTexCoord2f(0f, 1f); gl.glVertex3f( 1, -1,  1);
        gl.glTexCoord2f(1f, 1f); gl.glVertex3f(-1, -1,  1);

        gl.glEnd();

        if(renderOptions.getGlTexture() != null) {
            panelGL.getTexture(renderOptions.getGlTexture()).disable(gl);
        }
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
    public void updateRotation(String identifier, ShapeType shape, TransformSet rotation, QuaternionTransformSet quaternionRotation) {
        renderables.get(identifier).setRotation(rotation);
        renderables.get(identifier).setQuaternionRotation(quaternionRotation);
    }

    @Override
    public void updateSprite(String identifier, ShapeType shape, Sprite sprite) {

    }

    @Override
    public void updateColor(String identifier, ShapeType shape, ColorObject color) {
        renderables.get(identifier).setColor(color);
    }

    @Override
    public void updateGlTexture(String identifier, ShapeType shape, String glTexture) {
        renderables.get(identifier).setGlTexture(glTexture);
    }

}