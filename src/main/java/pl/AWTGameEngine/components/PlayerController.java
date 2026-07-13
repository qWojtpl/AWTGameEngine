package pl.AWTGameEngine.components;

import physx.PxTopLevelFunctions;
import physx.character.*;
import physx.common.PxVec3;
import physx.physics.PxScene;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.enums.KeyCode;
import pl.AWTGameEngine.engine.helpers.MovementHelper;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.transform.TransformSet;
import pl.AWTGameEngine.windows.Window;

@ComponentGL
public class PlayerController extends ObjectComponent {

    private PhysXManager.PhysXScene physicsScene;
    private PxControllerManager manager;
    private PxController controller;
    private float verticalVelocity = 0;

    //
    private float radius = 0.5f;
    private float height = 1.8f;
    private float cameraHeight = 1.6f;
    private float stepOffset = 0.5f;
    private float speed = 30f;
    private float jumpForce = 30f;

    public PlayerController(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        PhysXManager physXManager = PhysXManager.getInstance();
        this.physicsScene = physXManager.getScene(getScene());
        this.manager = PxTopLevelFunctions.CreateControllerManager(physicsScene.getPxScene());
        PxCapsuleControllerDesc desc = new PxCapsuleControllerDesc();
        desc.setRadius(radius);
        desc.setHeight(height);
        desc.setStepOffset(stepOffset);
        desc.setMaterial(physXManager.getDefaultMaterial());
        PxVec3 up = new PxVec3(0, 1, 0);
        desc.setUpDirection(up);
        this.controller = manager.createController(desc);
        desc.destroy();
        up.destroy();
    }

    @Override
    public void onRemoveComponent() {
        this.controller.release();
        this.manager.release();
    }

    @Override
    public void onPhysicsUpdate() {
        if(this.controller == null || !(getWindow() instanceof Window)) {
            return;
        }
        MovementHelper.handleRotation((Window) getWindow(), getMouseListener(), getCamera(), getObject());
        double[] movement = calculateMovement();
        handleMovement(movement[0], movement[1]);
    }

    private double[] calculateMovement() {
        TransformSet rotation = getCamera().getRotation();
        double yawRad = Math.toRadians(rotation.getY());

        double dirX = Math.sin(yawRad);
        double dirZ = -Math.cos(yawRad);

        double rightX = Math.cos(yawRad);
        double rightZ = Math.sin(yawRad);

        double forwardInput = 0, rightInput = 0;

        if(getKeyListener().hasPressedKey(KeyCode.W)) {
            forwardInput = 1;
        }
        if(getKeyListener().hasPressedKey(KeyCode.S)) {
            forwardInput = -1;
        }
        if(getKeyListener().hasPressedKey(KeyCode.A)) {
            rightInput = -1;
        }
        if(getKeyListener().hasPressedKey(KeyCode.D)) {
            rightInput = 1;
        }

        double moveX = (dirX * forwardInput + rightX * rightInput) * speed;
        double moveZ = (dirZ * forwardInput + rightZ * rightInput) * speed;

        return new double[]{moveX, moveZ};
    }

    private void handleMovement(double moveX, double moveZ) {
        float delta = (float) physicsScene.getPreviousStep();
        if(verticalVelocity == 0) {
            if(getKeyListener().hasPressedKey(KeyCode.SPACE)) {
                verticalVelocity += jumpForce;
            }
        }
        verticalVelocity += physicsScene.getGravity() * delta;
        PxVec3 moveVector = new PxVec3((float) (moveX * delta), verticalVelocity * delta, (float) (moveZ * delta));
        PxControllerFilters filters = new PxControllerFilters();
        PxControllerCollisionFlags flags = controller.move(moveVector, 0.01f, delta, filters);
        if(flags.isSet(PxControllerCollisionFlagEnum.eCOLLISION_DOWN)) {
            verticalVelocity = 0;
        }
        moveVector.destroy();
        filters.destroy();
        getObject().getPosition().set(controller.getPosition().getX(), controller.getPosition().getY() + cameraHeight, controller.getPosition().getZ());
        getCamera().setPosition(getObject().getPosition().clone());
    }

    @SaveState(name = "radius")
    public float getRadius() {
        return this.radius;
    }

    @FromXML
    public void setRadius(float radius) {
        this.radius = radius;
    }

    @SaveState(name = "height")
    public float getHeight() {
        return this.height;
    }

    @FromXML
    public void setHeight(float height) {
        this.height = height;
    }

    @SaveState(name = "cameraHeight")
    public float getCameraHeight() {
        return this.height;
    }

    @FromXML
    public void setCameraHeight(float cameraHeight) {
        this.cameraHeight = cameraHeight;
    }

    @SaveState(name = "stepOffset")
    public float getStepOffset() {
        return this.stepOffset;
    }

    @FromXML
    public void setStepOffset(float offset) {
        this.stepOffset = offset;
    }

    @SaveState(name = "speed")
    public float getSpeed() {
        return this.speed;
    }

    @FromXML
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @SaveState(name = "jumpForce")
    public float getJumpForce() {
        return this.jumpForce;
    }

    @FromXML
    public void setJumpForce(float jumpForce) {
        this.jumpForce = jumpForce;
    }

}
