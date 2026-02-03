package pl.AWTGameEngine.engine;

import physx.PxTopLevelFunctions;
import physx.common.*;
import physx.cooking.PxCookingParams;
import physx.physics.*;
import pl.AWTGameEngine.components.Vehicle;
import pl.AWTGameEngine.scenes.Scene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class PhysXManager {

    private static PhysXManager INSTANCE;
    private final static int NUM_THREADS = 8;
    private final static int VERSION = PxTopLevelFunctions.getPHYSICS_VERSION();
    private final PxDefaultAllocator allocator;
    private final PxDefaultErrorCallback errorCb;
    private final PxFoundation foundation;
    private final PxTolerancesScale tolerances;
    private final PxCookingParams cookingParams;
    private final PxPhysics physics;
    private final PxDefaultCpuDispatcher cpuDispatcher;
    private final PxVec3 gravityVector;
    private final PxShapeFlags shapeFlags;
    private HashMap<Scene, PhysXScene> scenes = new HashMap<>();
    private PxMaterial defaultMaterial;

    PhysXManager() {
        allocator = new PxDefaultAllocator();
        errorCb = new PxDefaultErrorCallback();
        foundation = PxTopLevelFunctions.CreateFoundation(VERSION, allocator, errorCb);
        tolerances = new PxTolerancesScale();
        cookingParams = new PxCookingParams(tolerances);
        physics = PxTopLevelFunctions.CreatePhysics(VERSION, foundation, tolerances);
        cpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(NUM_THREADS);
        gravityVector = new PxVec3(0f, -9.807f, 0f);
        shapeFlags = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value | PxShapeFlagEnum.eSIMULATION_SHAPE.value));
    }

    public void init() {
        Logger.info("PhysX loaded, version " + getVersionString());
        defaultMaterial = physics.createMaterial(0.5f, 0.5f, 0.5f);
    }

    public void createScene(Scene scene) {
        PhysXScene physXScene = new PhysXScene();
        physXScene.init();
        scenes.putIfAbsent(scene, physXScene);
    }

    public void simulateFrame(Scene scene, double fps) {
        float step = 1f / ((float) fps / 6);
        PhysXScene physXScene = scenes.get(scene);
        for(Vehicle vehicle : physXScene.vehicles) {
            vehicle.getPxVehicle().step(step, vehicle.getContext());
        }
        physXScene.pxScene.simulate(step);
        physXScene.pxScene.fetchResults(true);
    }

    public void registerVehicle(Scene scene, Vehicle vehicle) {
        scenes.get(scene).vehicles.add(vehicle);
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

    public PxScene getPxScene(Scene scene) {
        return this.scenes.get(scene).pxScene;
    }

    public PxShapeFlags getShapeFlags() {
        return this.shapeFlags;
    }

    public PxCookingParams getCookingParams() {
        return this.cookingParams;
    }

    public PxMaterial getDefaultMaterial() {
        return this.defaultMaterial;
    }

    public void cleanup() {
        defaultMaterial.destroy();
        shapeFlags.destroy();
        for(PhysXScene physXScene : scenes.values()) {
            physXScene.destroy();
        }
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
            INSTANCE.init();
        }
        return INSTANCE;
    }

    class PhysXScene {

        private PxScene pxScene;
        private PxSceneDesc pxSceneDesc;
        private final List<Vehicle> vehicles = new ArrayList<>();

        public void init() {
            pxSceneDesc = new PxSceneDesc(tolerances);
            pxSceneDesc.setGravity(gravityVector);
            pxSceneDesc.setCpuDispatcher(cpuDispatcher);
            pxSceneDesc.setFilterShader(PxTopLevelFunctions.DefaultFilterShader());
            pxSceneDesc.setSolverType(PxSolverTypeEnum.ePGS);
            pxScene = physics.createScene(pxSceneDesc);
        }

        public void destroy() {
            pxSceneDesc.destroy();
            pxScene.release();
        }

    }

}
