package pl.AWTGameEngine.objects.render.shaders;

import com.jogamp.opengl.GL4;

public class XRayShader extends Shader {

    XRayShader() {
        super("shaders/xray");
    }

    @Override
    public void use(ShaderUseContext useContext) {
        setFloatMatrixValue(getLocation("viewProj"), useContext.getViewProjection());
        setFloatMatrixValue(getLocation("model"), useContext.getModel());

        useContext.getGl4().glDepthFunc(GL4.GL_GREATER);
        useContext.getGl4().glDepthMask(false);
        useContext.getGl4().glDrawArrays(GL4.GL_TRIANGLES, 0, useContext.getShape().getVertexCount());
        useContext.getGl4().glDepthFunc(GL4.GL_LESS);
        useContext.getGl4().glDepthMask(true);
    }
}
