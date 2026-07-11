package pl.AWTGameEngine.components;

import physx.PxTopLevelFunctions;
import physx.character.*;
import physx.common.PxVec3;
import physx.physics.PxScene;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.transform.TransformSet;

@ComponentGL
public class PlayerController extends ObjectComponent {

    private PhysXManager physXManager;
    private PxControllerManager manager;
    private PxController controller;
    private float verticalVelocity = 0;

    public PlayerController(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        this.physXManager = PhysXManager.getInstance();
        this.manager = PxTopLevelFunctions.CreateControllerManager(physXManager.getScene(getScene()).getPxScene());
        PxCapsuleControllerDesc desc = new PxCapsuleControllerDesc();
        desc.setRadius(0.5f);
        desc.setHeight(1.8f);
        desc.setStepOffset(0.5f);
        desc.setMaterial(physXManager.getDefaultMaterial());
        PxVec3 up = new PxVec3(0, 1, 0);
        desc.setUpDirection(up);
        this.controller = manager.createController(desc);
        desc.destroy();
        up.destroy();
    }

    @Override
    public void onPhysicsUpdate() {
        if(this.controller == null) {
            return;
        }
        PhysXManager.PhysXScene scene = physXManager.getScene(getScene());

        float delta = 0.016f;
        verticalVelocity += scene.getGravity() * delta;
        PxVec3 moveVector = new PxVec3(30 * delta, verticalVelocity * delta, -30 * delta);
        PxControllerFilters filters = new PxControllerFilters();
        PxControllerCollisionFlags flags = controller.move(moveVector, 0.01f, delta, filters);
        if(flags.isSet(PxControllerCollisionFlagEnum.eCOLLISION_DOWN)) {
            verticalVelocity = 0;
        }
        moveVector.destroy();
        filters.destroy();
        getObject().getPosition().set(controller.getPosition().getX(), controller.getPosition().getY() + 1.8, controller.getPosition().getZ());
        getCamera().setPosition(getObject().getPosition().clone());
    }

}
