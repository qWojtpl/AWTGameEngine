package pl.AWTGameEngine.objects.render.shaders;

import com.jogamp.opengl.GL4;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.Shape;

public class BillboardShader extends Shader {

    BillboardShader() {
        super("shaders/billboard");
    }

    @Override
    public void use(GL4 gl, RenderOptions3D renderOptions, float[] viewProj, float[] model, Shape shape) {
        setFloatMatrixValue(getLocation("viewProj"), viewProj);
        setFloatMatrixValue(getLocation("model"), model);

        setFloatValue(getLocation("opacity"), renderOptions.getOpacity());
        setFloatValue(getLocation("repeat"), renderOptions.getRepeatTexture());
    }

}
