package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.Vehicle;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.KeyCode;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.TransformSet;

@DefaultComponent
@WebComponent
public class TopDownVehicleSteer extends ObjectComponent {

    private Vehicle.TopDown vehicle;
    private float torque = 0.3f;
    private float steer = 0;

    public TopDownVehicleSteer(GameObject object) {
        super(object);
    }

    @Override
    public void onSerializationFinish() {
        vehicle = (Vehicle.TopDown) getObject().getComponentByClass(Vehicle.TopDown.class);
    }

    @Override
    public void onUpdate() {
        torque = 0;
        steer = 0;
        if(getKeyListener().hasPressedKey(KeyCode.W.value)) {
            torque = 1;
        }
        if(getKeyListener().hasPressedKey(KeyCode.A.value)) {
            steer = -1;
        }
        if(getKeyListener().hasPressedKey(KeyCode.S.value)) {
            torque = -1;
        }
        if(getKeyListener().hasPressedKey(KeyCode.D.value)) {
            steer = 1;
        }
    }

    @Override
    public void onPhysicsUpdate() {
        vehicle.getPxVehicle().getCommandState().setThrottle(torque);
        vehicle.getPxVehicle().getCommandState().setSteer(steer);
        System.out.println("Torque: " + torque);
        System.out.println("Steer: " + steer);
    }


}
