package pl.AWTGameEngine.engine;

import physx.PxTopLevelFunctions;
import physx.common.*;
import physx.physics.*;

public class PhysXManager {

    private final int NUM_THREADS = 4;
    private final int VERSION = PxTopLevelFunctions.getPHYSICS_VERSION();
    private final PxDefaultAllocator allocator = new PxDefaultAllocator();
    private final PxDefaultErrorCallback errorCb = new PxDefaultErrorCallback();
    private final PxFoundation foundation = PxTopLevelFunctions.CreateFoundation(VERSION, allocator, errorCb);
    private final PxTolerancesScale tolerances = new PxTolerancesScale();
    private final PxPhysics physics = PxTopLevelFunctions.CreatePhysics(VERSION, foundation, tolerances);
    private final PxDefaultCpuDispatcher cpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(NUM_THREADS);
    private final PxVec3 gravityVector = new PxVec3(0f, -9.81f, 0f);
    private PxSceneDesc sceneDesc;
    private PxScene scene;
    private final PxShapeFlags shapeFlags = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value | PxShapeFlagEnum.eSIMULATION_SHAPE.value));

    public void init() {

        Logger.log(0, "PhysX loaded, version " + getVersionString());

        sceneDesc = new PxSceneDesc(tolerances);
        sceneDesc.setGravity(gravityVector);
        sceneDesc.setCpuDispatcher(cpuDispatcher);
        sceneDesc.setFilterShader(PxTopLevelFunctions.DefaultFilterShader());
        scene = physics.createScene(sceneDesc);

//        // create a few temporary objects used during setup
//
//        // clean up temp objects
//        groundGeometry.destroy();
//        boxGeometry.destroy();
//        tmpFilterData.destroy();
//        tmpPose.destroy();
//        tmpVec.destroy();
//        shapeFlags.destroy();
//        sceneDesc.destroy();
//        tolerances.destroy();
//
//        // box starts at a height of 5
//        float boxHeight = box.getGlobalPose().getP().getY();
//        Logger.log(0, boxHeight + " == 5f +- " + 0.0001f);
//
//        // run physics simulation
//        for (int i = 0; i <= 500; i++) {
//            scene.simulate(1f/60f);
//            scene.fetchResults(true);
//
//            boxHeight = box.getGlobalPose().getP().getY();
//            if (i % 10 == 0) {
//                System.out.println("Step " + i + ": h = " + boxHeight);
//            }
//        }
//
//        // box should rest on the ground
//
//        Logger.log(0,  "1f == " + boxHeight + " +- 0.0001f");
//
//        // cleanup stuff
//        scene.removeActor(ground);
//        ground.release();
//        groundShape.release();
//
//        scene.removeActor(box);
//        box.release();
//        boxShape.release();
//
//        scene.release();
//        material.release();
//        physics.release();
//        foundation.release();
//        errorCb.destroy();
//        allocator.destroy();
    }

    private String getVersionString() {
        int versionMajor = VERSION >> 24;
        int versionMinor = (VERSION >> 16) & 0xff;
        int versionMicro = (VERSION >> 8) & 0xff;

        return versionMajor + "." + versionMinor + "." + versionMicro;
    }

    public PxPhysics getPxPhysics() {
        return this.physics;
    }

    public PxScene getPxScene() {
        return this.scene;
    }

    public PxShapeFlags getShapeFlags() {
        return this.shapeFlags;
    }

    public void cleanup() {
        shapeFlags.destroy();
        sceneDesc.destroy();
        scene.release();
        gravityVector.destroy();
        cpuDispatcher.destroy();
        foundation.release();
        tolerances.destroy();
        foundation.release();
        errorCb.destroy();
        allocator.destroy();
    }

}
