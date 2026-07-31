package pl.AWTGameEngine.engine.helpers;

public class SkyboxHelper {

    private final static float[] skyboxVertices = new float[]{
            -1.0f,  1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            -1.0f, -1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f, -1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f, -1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f,  1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            -1.0f,  1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,

            -1.0f, -1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            -1.0f, -1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            -1.0f,  1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            -1.0f,  1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            -1.0f,  1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            -1.0f, -1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,

            1.0f, -1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f, -1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f,  1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f,  1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f,  1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f, -1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,

            -1.0f, -1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            -1.0f,  1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f,  1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f,  1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f, -1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            -1.0f, -1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,

            -1.0f,  1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f,  1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f,  1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f,  1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            -1.0f,  1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            -1.0f,  1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,

            -1.0f, -1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            -1.0f, -1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f, -1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f, -1.0f, -1.0f, 0f, 0f, 0f, 0f, 0f,
            -1.0f, -1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f,
            1.0f, -1.0f,  1.0f, 0f, 0f, 0f, 0f, 0f
    };

    public static float[] getSkyboxVertices() {
        return skyboxVertices;
    }

}
