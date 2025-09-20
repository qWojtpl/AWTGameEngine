package pl.AWTGameEngine.engine;

import physx.PxTopLevelFunctions;
import physx.common.*;
import physx.physics.*;

public final class PhysXManager {

    private static PhysXManager INSTANCE;
    private final static int NUM_THREADS = 4;
    private final static int VERSION = PxTopLevelFunctions.getPHYSICS_VERSION();
    private final PxDefaultAllocator allocator;
    private final PxDefaultErrorCallback errorCb;
    private final PxFoundation foundation;
    private final PxTolerancesScale tolerances;
    private final PxPhysics physics;
    private final PxDefaultCpuDispatcher cpuDispatcher;
    private final PxVec3 gravityVector;
    private final PxShapeFlags shapeFlags;
    private PxSceneDesc sceneDesc;
    private PxScene scene;

    PhysXManager() {
        allocator = new PxDefaultAllocator();
        errorCb = new PxDefaultErrorCallback();
        foundation = PxTopLevelFunctions.CreateFoundation(VERSION, allocator, errorCb);
        tolerances = new PxTolerancesScale();
        physics = PxTopLevelFunctions.CreatePhysics(VERSION, foundation, tolerances);
        cpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(NUM_THREADS);
        gravityVector = new PxVec3(0f, -9.81f, 0f);
        shapeFlags = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value | PxShapeFlagEnum.eSIMULATION_SHAPE.value));
    }

    public void init() {
        Logger.info("PhysX loaded, version " + getVersionString());

        sceneDesc = new PxSceneDesc(tolerances);
        sceneDesc.setGravity(gravityVector);
        sceneDesc.setCpuDispatcher(cpuDispatcher);
        sceneDesc.setFilterShader(PxTopLevelFunctions.DefaultFilterShader());
        scene = physics.createScene(sceneDesc);
    }

    public void simulateFrame(double fps) {
        getPxScene().simulate(1f/((float) fps / 6));
        getPxScene().fetchResults(true);
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
        physics.destroy();
        tolerances.destroy();
        foundation.release();
        errorCb.destroy();
        allocator.destroy();
    }

    public static PhysXManager getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PhysXManager();
        }
        return INSTANCE;
    }

}
