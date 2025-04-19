package pl.AWTGameEngine.scenes;

public class SceneOptions {

    private final String title;
    private final boolean fullscreen;
    private final int renderFPS;
    private final int updateFPS;
    private final boolean sameSize;

    public SceneOptions(String title, boolean fullscreen, int renderFPS, int updateFPS, boolean sameSize) {
        this.title = title;
        this.fullscreen = fullscreen;
        this.renderFPS = renderFPS;
        this.updateFPS = updateFPS;
        this.sameSize = sameSize;
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

    public boolean isSameSize() {
        return this.sameSize;
    }

}
