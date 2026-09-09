package pl.AWTGameEngine.objects.render.shaders;

import com.jogamp.opengl.GL4;
import pl.AWTGameEngine.objects.render.Camera;
import pl.AWTGameEngine.objects.render.GLShape;
import pl.AWTGameEngine.objects.render.RenderOptions3D;

public class ShaderUseContext {

    private GL4 gl;
    private RenderOptions3D renderOptions;
    private float[] viewProj;
    private float[] model;
    private GLShape glShape;
    private Camera camera;

    public GL4 getGl4() {
        return this.gl;
    }

    public ShaderUseContext setGL4(GL4 gl) {
        this.gl = gl;
        return this;
    }

    public RenderOptions3D getRenderOptions() {
        return this.renderOptions;
    }

    public ShaderUseContext setRenderOptions(RenderOptions3D renderOptions) {
        this.renderOptions = renderOptions;
        return this;
    }

    public float[] getViewProjection() {
        return this.viewProj;
    }

    public ShaderUseContext setViewProjection(float[] viewProj) {
        this.viewProj = viewProj;
        return this;
    }

    public float[] getModel() {
        return this.model;
    }

    public ShaderUseContext setModel(float[] model) {
        this.model = model;
        return this;
    }

    public GLShape getShape() {
        return this.glShape;
    }

    public ShaderUseContext setShape(GLShape glShape) {
        this.glShape = glShape;
        return this;
    }

    public Camera getCamera() {
        return this.camera;
    }

    public ShaderUseContext setCamera(Camera camera) {
        this.camera = camera;
        return this;
    }

}
