package pl.AWTGameEngine.objects.render.shaders;

import com.jogamp.opengl.GL4;
import pl.AWTGameEngine.engine.Shaders;
import pl.AWTGameEngine.objects.transform.Vector3;
import pl.AWTGameEngine.windows.BaseWindow;

/**
 * Shader class is used to create custom shaders.
 * Every shader instance is a singleton in window area. To obtain shader, use {@link Shaders#of(BaseWindow, Class)}.<br>
 * You can use helper methods, such as {@link #setFloatValue(int, float)} or {@link #setFloatMatrixValue(int, float[])},
 * or just use GL context.<br>
 * For reference, check out {@link DefaultShader} or {@link XRayShader} shaders.<br>
 * Shader is compiled before its first use.<br>
 * Vertex shader layout:
 * <pre>
 * {@code
 * layout(location = 0) in vec3 aPos;
 * layout(location = 1) in vec3 normal;
 * layout(location = 2) in vec2 aUV;
 * }
 * </pre>
 */
public abstract class Shader  {

    private final String name;
    private int program = -1;
    private GL4 currentContext;

    /**
     *
     * @param name Path to shader files. For example, if shader has <code>billboard.frag</code> and <code>billboard.vert</code>
     *             files in <code>shaders</code> directory, just pass "shaders/billboard" as shader name.
     */
    Shader(String name) {
        this.name = name;
    }

    /**
     * Method is invoked everytime an object wants to render.
     */
    public abstract void use(ShaderUseContext useContext);

    public int getLocation(String name) {
        return currentContext.glGetUniformLocation(getProgram(), name);
    }

    public void setFloatValue(int location, float value) {
        currentContext.glUniform1f(location, value);
    }

    public void setFloatMatrixValue(int location, float[] value) {
        currentContext.glUniformMatrix4fv(location, 1, false, value, 0);
    }

    public void setVec3Value(int location, Vector3 vector3) {
        setVec3Value(location, (float) vector3.getX(), (float) vector3.getY(), (float) vector3.getZ());
    }

    public void setVec3Value(int location, float x, float y, float z) {
        currentContext.glUniform3f(location, x, y, z);
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

    private void setProgram(int program) {
        this.program = program;
    }

    public void setCurrentContext(GL4 context) {
        this.currentContext = context;
        context.glUseProgram(getProgram());
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }

}
