package pl.AWTGameEngine.scenes;

public class SceneOptions {

    private String title = "Default window title";
    private boolean fullscreen = false;
    private int renderFPS = 60;
    private int updateFPS = 60;
    private int multiplier = 1;

    public SceneOptions() {

    }

    public SceneOptions(String title, boolean fullscreen, int renderFPS, int updateFPS, int multiplier) {
        this.title = title;
        this.fullscreen = fullscreen;
        this.renderFPS = renderFPS;
        this.updateFPS = updateFPS;
        this.multiplier = multiplier;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isFullscreen() {
        return this.fullscreen;
    }

    public int getRenderFPS() {
        return this.renderFPS;
    }

    public int getUpdateFPS() {
        return this.updateFPS;
    }

    public int getMultiplier() {
        return this.multiplier;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public void setRenderFPS(int renderFPS) {
        this.renderFPS = renderFPS;
    }

    public void setUpdateFPS(int updateFPS) {
        this.updateFPS = updateFPS;
    }

}
