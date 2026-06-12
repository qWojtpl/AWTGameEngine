package pl.AWTGameEngine.objects.render;

public class Shape {

    private final String path;
    private final int vao;
    private final int vbo;

    public Shape(String path, int vao, int vbo) {
        this.path = path;
        this.vao = vao;
        this.vbo = vbo;
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

}
