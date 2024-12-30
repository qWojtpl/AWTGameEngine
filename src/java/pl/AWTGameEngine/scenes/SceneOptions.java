package pl.AWTGameEngine.scenes;

public class SceneOptions {

    private final String title;
    private final boolean fullscreen;
    private final int renderFPS;
    private final int updateFPS;
    private final int multiplier;

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

}
