package pl.AWTGameEngine.scenes;

import java.util.Arrays;
import java.util.List;

public class SceneOptions {

    private final String title;
    private final boolean fullscreen;
    private final int renderFPS;
    private final int updateFPS;
    private final int physicsFPS;
    private final boolean sameSize;
    private final List<String> packages;

    public SceneOptions(String title, boolean fullscreen, int renderFPS, int updateFPS, int physicsFPS, boolean sameSize, String packages) {
        this.title = title;
        this.fullscreen = fullscreen;
        this.renderFPS = renderFPS;
        this.updateFPS = updateFPS;
        this.physicsFPS = physicsFPS;
        this.sameSize = sameSize;
        String[] split = packages.replace(" ", "").split(",");
        this.packages = Arrays.stream(split).toList();
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

    public List<String> getPackages() {
        return this.packages;
    }

}
