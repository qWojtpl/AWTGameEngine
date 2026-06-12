package pl.AWTGameEngine.engine.enums;

import pl.AWTGameEngine.engine.panels.*;

public enum RenderEngine {

    DEFAULT(DefaultPanel.class),
    WEB(WebPanel.class),
    OPENGL(PanelGL.class);

    private final Class<? extends PanelObject> panelClass;

    RenderEngine(Class<? extends PanelObject> clazz) {
        this.panelClass = clazz;
    }

    public Class<? extends PanelObject> getPanelClass() {
        return this.panelClass;
    }

}