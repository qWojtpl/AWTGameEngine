package pl.AWTGameEngine.objects.render;

public class Shape {

    private final String path;
    private final int vao;
    private final int vbo;
    private final int vertexCount;

    public Shape(String path, int vao, int vbo, int vertexCount) {
        this.path = path;
        this.vao = vao;
        this.vbo = vbo;
        this.vertexCount = vertexCount;
    }

    public String getPath() {
        return this.path;
    }

    public int getVao() {
        return this.vao;
    }

    public int getVbo() {
        return this.vbo;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

}
