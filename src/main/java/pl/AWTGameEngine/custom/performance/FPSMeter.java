package pl.AWTGameEngine.custom.performance;

import pl.AWTGameEngine.annotations.ComponentFX;
import pl.AWTGameEngine.annotations.ComponentGL;
import pl.AWTGameEngine.annotations.DefaultComponent;
import pl.AWTGameEngine.annotations.WebComponent;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.objects.GameObject;

@ComponentGL
@ComponentFX
@WebComponent
@DefaultComponent
public class FPSMeter extends ObjectComponent {

    public FPSMeter(GameObject object) {
        super(object);
    }

    @Override
    public void onEverySecond() {
        System.out.println("Update FPS: " + getWindow().getUpdateLoop().getActualFps());
        System.out.println("Render FPS: " + getWindow().getRenderLoop().getActualFps());
        System.out.println("Physics FPS: " + getWindow().getPhysicsLoop().getActualFps());
        System.out.println("Current scene: " + getWindow().getCurrentScene().getName());
    }

}
