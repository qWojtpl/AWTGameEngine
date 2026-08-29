package pl.AWTGameEngine.objects.render.shaders;

public class GUIShader extends Shader {

    GUIShader() {
        super("shaders/gui");
    }

    @Override
    public void use(ShaderUseContext useContext) {
        setFloatMatrixValue(getLocation("viewProj"), useContext.getViewProjection());
        setFloatMatrixValue(getLocation("model"), useContext.getModel());

        setFloatValue(getLocation("opacity"), useContext.getRenderOptions().getOpacity());
        setFloatValue(getLocation("repeat"), useContext.getRenderOptions().getRepeatTexture());
    }
}
