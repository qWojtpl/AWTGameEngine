package pl.AWTGameEngine.objects.render.shaders;

import com.jogamp.opengl.GL4;
import pl.AWTGameEngine.engine.Shaders;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.Shape;


public abstract class Shader  {

    private final String name;
    private int program = -1;
    private GL4 currentContext;

    Shader(String name) {
        this.name = name;
    }

    public abstract void use(GL4 gl, RenderOptions3D renderOptions, float[] viewProj, float[] model, Shape shape);

    public int getLocation(String name) {
        return currentContext.glGetUniformLocation(getProgram(), name);
    }

    public void setFloatValue(int location, float value) {
        currentContext.glUniform1f(location, value);
    }

    public void setFloatMatrixValue(int location, float[] value) {
        currentContext.glUniformMatrix4fv(location, 1, false, value, 0);
    }

    public String getName() {
        return this.name;
    }

    public int getProgram() {
        if(this.program == -1) {
            setProgram(Shaders.getProgram(currentContext, name));
        }
        return this.program;
    }

    public void setProgram(int program) {
        this.program = program;
    }

    public void setCurrentContext(GL4 context) {
        this.currentContext = context;
        context.glUseProgram(getProgram());
    }

    @Override
    public String toString() {
        return getName();
    }

}
