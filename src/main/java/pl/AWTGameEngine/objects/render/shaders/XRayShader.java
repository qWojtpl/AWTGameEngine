package pl.AWTGameEngine.objects.render.shaders;

import com.jogamp.opengl.GL4;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.Shape;

public class XRayShader extends Shader {

    XRayShader() {
        super("shaders/xray");
    }

    @Override
    public void use(GL4 gl, RenderOptions3D renderOptions, float[] viewProj, float[] model, Shape shape) {
        setFloatMatrixValue(getLocation("viewProj"), viewProj);
        setFloatMatrixValue(getLocation("model"), model);

        gl.glDepthFunc(GL4.GL_GREATER);
        gl.glDepthMask(false);
        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, shape.getVertexCount());
        gl.glDepthFunc(GL4.GL_LESS);
        gl.glDepthMask(true);
    }
}
