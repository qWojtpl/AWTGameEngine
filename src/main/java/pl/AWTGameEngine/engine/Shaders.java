package pl.AWTGameEngine.engine;

import com.jogamp.opengl.GL4;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.exceptions.ShaderCompileException;
import pl.AWTGameEngine.objects.render.shaders.Shader;
import pl.AWTGameEngine.windows.BaseWindow;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class Shaders {

    private static final HashMap<BaseWindow, HashMap<Class<? extends Shader>, Shader>> shaderRegistry = new HashMap<>();
    //todo: programs with BaseWindow
    private static final HashMap<String, Integer> programs = new HashMap<>();

    /**
     *
     * @param clazz Class of shader
     * @return      Singleton of shader class
     */
    public static Shader of(BaseWindow window, Class<? extends Shader> clazz) {
        if(!shaderRegistry.containsKey(window)) {
            shaderRegistry.put(window, new HashMap<>());
        }
        if(!shaderRegistry.get(window).containsKey(clazz)) {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                shaderRegistry.get(window).put(clazz, (Shader) constructor.newInstance());
            } catch (Exception e) {
                Logger.exception("Cannot get shader from " + clazz.getCanonicalName(), e);
            }
        }

        return shaderRegistry.get(window).get(clazz);
    }

    private static int createProgram(GL4 gl, String shaderName) {
        Logger.info("Compiling shader: " + shaderName + "...");
        int vs = compileShader(gl, GL4.GL_VERTEX_SHADER, getShaderFile(shaderName + ".vert"));
        int fs = compileShader(gl, GL4.GL_FRAGMENT_SHADER, getShaderFile(shaderName + ".frag"));

        int program = gl.glCreateProgram();
        gl.glAttachShader(program, vs);
        gl.glAttachShader(program, fs);
        gl.glLinkProgram(program);

        int[] status = new int[1];
        gl.glGetProgramiv(program, GL4.GL_LINK_STATUS, status, 0);
        if (status[0] == 0) {
            throw new ShaderCompileException(getProgramLog(gl, program));
        }

        gl.glDeleteShader(vs);
        gl.glDeleteShader(fs);
        return program;
    }

    public static int getProgram(GL4 gl, String shaderName) {
        if(!programs.containsKey(shaderName)) {
            int newProgram = createProgram(gl, shaderName);
            programs.put(shaderName, newProgram);
            return newProgram;
        }
        return programs.get(shaderName);
    }

    private static String getShaderFile(String fileName) {
        return String.join("\n", Dependencies.getResourceManager().getResource(fileName));
    }

    private static int compileShader(GL4 gl, int type, String src) {
        int s = gl.glCreateShader(type);
        gl.glShaderSource(s, 1, new String[]{src}, new int[]{src.length()}, 0);
        gl.glCompileShader(s);

        int[] status = new int[1];
        gl.glGetShaderiv(s, GL4.GL_COMPILE_STATUS, status, 0);
        if(status[0] == 0) {
            throw new ShaderCompileException(getShaderLog(gl, s));
        }
        return s;
    }

    private static String getShaderLog(GL4 gl, int s) {
        byte[] buf = new byte[1024];
        gl.glGetShaderInfoLog(s, buf.length, null, 0, buf, 0);
        return new String(buf);
    }

    private static String getProgramLog(GL4 gl, int p) {
        byte[] buf = new byte[1024];
        gl.glGetProgramInfoLog(p, buf.length, null, 0, buf, 0);
        return new String(buf);
    }

    public static void disposePrograms(BaseWindow window, GL4 gl) {
        for(int program : programs.values()) {
            gl.glDeleteProgram(program);
        }
        shaderRegistry.remove(window);
    }


}
