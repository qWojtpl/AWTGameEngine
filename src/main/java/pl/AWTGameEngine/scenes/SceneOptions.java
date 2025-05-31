package pl.AWTGameEngine.scenes;

public class SceneOptions {

    private final String title;
    private final boolean fullscreen;
    private final int renderFPS;
    private final int updateFPS;
    private final int physicsFPS;
    private final boolean sameSize;

    public SceneOptions(String title, boolean fullscreen, int renderFPS, int updateFPS, int physicsFPS, boolean sameSize) {
        this.title = title;
        this.fullscreen = fullscreen;
        this.renderFPS = renderFPS;
        this.updateFPS = updateFPS;
        this.physicsFPS = physicsFPS;
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

    public int getPhysicsFPS() {
        return this.physicsFPS;
    }

    public boolean isSameSize() {
        return this.sameSize;
    }

}
