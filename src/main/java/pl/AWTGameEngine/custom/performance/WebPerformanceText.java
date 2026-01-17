package pl.AWTGameEngine.custom.performance;

import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.WebText;
import pl.AWTGameEngine.engine.loops.BaseLoop;
import pl.AWTGameEngine.objects.GameObject;

@WebComponent
public class WebPerformanceText extends WebText {

    public WebPerformanceText(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        setSize(18);
        setPadding(20);
        setColor("rgb(16,16,16)");
        setPattern("Update FPS: {updateFPS}<br>Render FPS: {renderFPS}<br>Physics FPS: {physicsFPS}<br>Net FPS: {netFPS}<br>Objects: {objects}<br>Total threads: {threads}<br>Registered global events: {events}");
    }

    @Override
    public void onEverySecond() {
        setValue("updateFPS", getFpsString(getWindow().getUpdateLoop()));
        setValue("renderFPS", getFpsString(getWindow().getRenderLoop()));
        setValue("physicsFPS", getFpsString(getWindow().getPhysicsLoop()));
        setValue("netFPS", getFpsString(getWindow().getNetLoop()));
        setValue("objects", getWindow().getCurrentScene().getGameObjects().size() + "");
        setValue("threads", Thread.getAllStackTraces().size() + "");
        setValue("events", getWindow().getCurrentScene().getSceneEventHandler().getNumberOfRegisteredEvents() + "");
    }

    private String getFpsString(BaseLoop loop) {
        return (int) loop.getActualFps() + " / " + (loop.getTargetFps() == 0 ? "∞" : (int) loop.getTargetFps() + "");
    }

}
