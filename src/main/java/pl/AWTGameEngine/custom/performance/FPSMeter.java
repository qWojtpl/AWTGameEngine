package pl.AWTGameEngine.custom.performance;

import pl.AWTGameEngine.annotations.components.types.ComponentFX;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
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
        System.out.println("Net FPS: " + getWindow().getNetLoop().getActualFps());
        System.out.println("Current scene: " + getWindow().getCurrentScene().getName());
    }

}
