package pl.AWTGameEngine.custom;

import pl.AWTGameEngine.annotations.components.types.ComponentFX;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.components.Vehicle;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.KeyCode;
import pl.AWTGameEngine.objects.GameObject;

@DefaultComponent
@WebComponent
@ComponentGL
@ComponentFX
public class VehicleSteer extends ObjectComponent {

    private Vehicle vehicle;
    private float torque = 0.3f;
    private int brake = 0;
    private float steer = 0;
    private float steerSensitivity = 1f;

    public VehicleSteer(GameObject object) {
        super(object);
    }

    @Override
    public void onSerializationFinish() {
        vehicle = (Vehicle) getObject().getComponentByClass(Vehicle.class);

    }

    @Override
    public void onUpdate() {
        torque = 0;
        steer = 0;
        brake = 0;
        if(getKeyListener().hasPressedKey(KeyCode.W.value)) {
            torque = 1;
        }
        if(getKeyListener().hasPressedKey(KeyCode.A.value)) {
            steer = steerSensitivity;
        }
        if(getKeyListener().hasPressedKey(KeyCode.S.value)) {
            brake = 5;
        }
        if(getKeyListener().hasPressedKey(KeyCode.D.value)) {
            steer = -steerSensitivity;
        }
    }

    @Override
    public void onPhysicsUpdate() {
        vehicle.getPxVehicle().getCommandState().setThrottle(torque);
        vehicle.getPxVehicle().getCommandState().setSteer(steer);
        vehicle.getPxVehicle().getCommandState().setNbBrakes(brake);

        int currentGear = vehicle.getPxVehicle().getEngineDriveState().getGearboxState().getCurrentGear();

        if(currentGear == 0 || currentGear == 1) {
            vehicle.getGearbox().setCurrentGear(2);
        }
        float omega = vehicle.getPxVehicle().getEngineDriveState().getEngineState().getRotationSpeed();
        float rpm = vehicle.omegaToRPM(omega);

        float speed = vehicle.getPxVehicle().getPhysXState().getPhysxActor().getRigidBody().getLinearVelocity().magnitude();

        if(speed > 3 * currentGear * currentGear) {
            vehicle.getGearbox().setCurrentGear(currentGear + 1);
        }

//        System.out.println("Torque: " + torque);
//        System.out.println("Steer: " + steer);
//        System.out.println("Gear: " + currentGear);
//        System.out.println("RPM: " + rpm);
//        System.out.println("Speed: " + speed);
    }


}
