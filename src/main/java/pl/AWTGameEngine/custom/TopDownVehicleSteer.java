package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.Vehicle;
import pl.AWTGameEngine.components.base.ObjectComponent;
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
        if(getKeyListener().hasPressedKey(87)) { // W
            torque += 0.01f;
        }
        if(getKeyListener().hasPressedKey(65)) { // A
            steer -= 0.01f;
        }
        if(getKeyListener().hasPressedKey(83)) { // S
            torque -= 0.01f;
        }
        if(getKeyListener().hasPressedKey(68)) { // D
            steer += 0.01f;
        }
        if(torque > 1) {
            torque = 1;
        } else if(torque < 0) {
            torque = 0;
        }
        if(steer > 1) {
            steer = 1;
        } else if(steer < -1) {
            steer = -1;
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
