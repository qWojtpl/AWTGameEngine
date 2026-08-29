package pl.AWTGameEngine.objects.render.shaders;

import com.jogamp.opengl.GL4;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.Shape;

public class ShaderUseContext {

    private GL4 gl;
    private RenderOptions3D renderOptions;
    private float[] viewProj;
    private float[] model;
    private Shape shape;

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

    public Shape getShape() {
        return this.shape;
    }

    public ShaderUseContext setShape(Shape shape) {
        this.shape = shape;
        return this;
    }

}
