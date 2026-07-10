package pl.AWTGameEngine.components;

import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.PxBoxGeometry;
import physx.geometry.PxGeometry;
import physx.physics.*;
import physx.vehicle2.*;
import pl.AWTGameEngine.annotations.components.management.*;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.components.types.DefaultComponent;
import pl.AWTGameEngine.annotations.components.types.WebComponent;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.helpers.RotationHelper;
import pl.AWTGameEngine.engine.helpers.VehicleHelper;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.transform.QuaternionTransformSet;
import pl.AWTGameEngine.objects.transform.TransformSet;

import java.util.*;

@ComponentGL
@Unique
public class Vehicle extends ObjectComponent {

    private final PhysXManager physXManager = PhysXManager.getInstance();
    protected final EngineDriveVehicle vehicle = new EngineDriveVehicle();
    private final PxVehiclePhysXSimulationContext context = new PxVehiclePhysXSimulationContext();

    private Engine engine;
    private Gearbox gearbox;
    private final List<Wheel> wheels = new ArrayList<>();

    private float mass = 870f;
    private float maxBrakeResponse = 2000f;
    private float maxHandBrakeResponse = 2500f;
    private float maxSteerResponse = 0.25f;

    public Vehicle(GameObject object) {
        super(object);
    }

    private void setEngine(Engine engine) {
        this.engine = engine;
    }

    private void setGearbox(Gearbox gearbox) {
        this.gearbox = gearbox;
    }

    public void registerWheel(Wheel wheel) {
        if(wheel.getId() == -1) {
            wheel.setId(wheels.size());
        }
        wheels.add(wheel);
    }

    public Gearbox getGearbox() {
        return this.gearbox;
    }

    public Engine getEngine() {
        return this.engine;
    }

    public List<Wheel> getRegisteredWheels() {
        return new ArrayList<>(wheels);
    }

    @SaveState(name = "mass")
    public float getMass() {
        return this.mass;
    }

    @FromXML
    public void setMass(float mass) {
        this.mass = mass;
    }

    @SaveState(name = "maxBrakeResponse")
    public float getMaxBrakeResponse() {
        return this.maxBrakeResponse;
    }

    @FromXML
    public void setMaxBrakeResponse(float maxBrakeResponse) {
        this.maxBrakeResponse = maxBrakeResponse;
    }

    @SaveState(name = "maxHandBrakeResponse")
    public float getMaxHandBrakeResponse() {
        return this.maxHandBrakeResponse;
    }

    @FromXML
    public void setMaxHandBrakeResponse(float maxHandBrakeResponse) {
        this.maxHandBrakeResponse = maxHandBrakeResponse;
    }

    @SaveState(name = "maxSteerResponse")
    public float getMaxSteerResponse() {
        return this.maxHandBrakeResponse;
    }

    @FromXML
    public void setMaxSteerResponse(float maxSteerResponse) {
        this.maxSteerResponse = maxSteerResponse;
    }

    private void initialize() {
        PxGeometry geometry = createGeometry();

        initializeMass();
        initializeAxle();
        initializeResponseParams();
        setBaseParams(vehicle.getBaseParams());
        for(Wheel wheel : wheels) {
            wheel.init();
        }
        setPhysxIntegrationParams(vehicle.getPhysXParams(), vehicle.getBaseParams().getAxleDescription(), geometry);
        setEngineDriveParams(vehicle.getEngineDriveParams());

        vehicle.initialize(
                physXManager.getPxPhysics(),
                physXManager.getCookingParams(),
                physXManager.getDefaultMaterial(),
                EngineDriveVehicleEnum.eDIFFTYPE_MULTIWHEELDRIVE
        );

        TransformSet initializationPose = getVehicleInitializationPose();

        var vehiclePose = new PxTransform(
                new PxVec3((float) initializationPose.getX(), (float) initializationPose.getY(), (float) initializationPose.getZ()),
                new PxQuat(PxIDENTITYEnum.PxIdentity));

        vehicle.getPhysXState().getPhysxActor().getRigidBody().setGlobalPose(vehiclePose);

        getWindow().getPhysicsLoop().addNextFrameOperation(() -> {
            physXManager.getPxScene(getScene()).addActor(vehicle.getPhysXState().getPhysxActor().getRigidBody());
        });

        vehicle.getEngineDriveState().getGearboxState().setCurrentGear(vehicle.getEngineDriveParams().getGearBoxParams().getNeutralGear() + 1);
        vehicle.getEngineDriveState().getGearboxState().setTargetGear(vehicle.getEngineDriveParams().getGearBoxParams().getNeutralGear() + 1);

        context.setToDefault();
        context.getFrame().setLngAxis(PxVehicleAxesEnum.ePosZ);
        context.getFrame().setLatAxis(PxVehicleAxesEnum.ePosX);
        context.getFrame().setVrtAxis(PxVehicleAxesEnum.ePosY);
        context.getScale().setScale(1f);
        context.setGravity(physXManager.getPxScene(getScene()).getGravity());
        context.setPhysxScene(physXManager.getPxScene(getScene()));
        context.setPhysxActorUpdateMode(PxVehiclePhysXActorUpdateModeEnum.eAPPLY_ACCELERATION);
        context.setPhysxUnitCylinderSweepMesh(
                PxVehicleTopLevelFunctions.VehicleUnitCylinderSweepMeshCreate(context.getFrame(), physXManager.getPxPhysics(), physXManager.getCookingParams()));


        physXManager.registerVehicle(getScene(), this);
        vehicle.getCommandState().setThrottle(1f);
        vehicle.getCommandState().setNbBrakes(0);
        vehicle.getCommandState().setSteer(0.3f);

    }

    protected PxGeometry createGeometry() {
        return new PxBoxGeometry(
                (float) getObject().getSize().getX(),
                (float) getObject().getSize().getY(),
                (float) getObject().getSize().getZ());
    }

    protected TransformSet getVehicleSize() {
        return getObject().getSize();
    }

    protected TransformSet getVehicleInitializationPose() {
        return getObject().getPosition();
    }

    private void initializeMass() {
        PxVehicleRigidBodyParams rigidBody = vehicle.getBaseParams().getRigidBodyParams();
        rigidBody.setMass(mass);

        double width  = getVehicleSize().getX();
        double height = getVehicleSize().getY();
        double length = getVehicleSize().getZ();

        float moiX = (float) ((1 / 12.0) * mass * (height * height + length * length));
        float moiY = (float) ((1 / 12.0) * mass * (width * width + length * length));
        float moiZ = (float) ((1 / 12.0) * mass * (width * width + height * height));
        PxVec3 moi = new PxVec3(moiX, moiY, moiZ);
        rigidBody.setMoi(moi);
        moi.destroy();
    }

    private void initializeAxle() {
        PxVehicleAxleDescription axle = vehicle.getBaseParams().getAxleDescription();
        HashMap<Integer, List<Wheel>> axleWheels = new HashMap<>();
        for(Wheel wheel : wheels) {
            List<Wheel> wheelList = axleWheels.getOrDefault(wheel.getAxleId(), new ArrayList<>());
            wheelList.add(wheel);
            axleWheels.put(wheel.getAxleId(), wheelList);
        }
        axle.setNbAxles(axleWheels.keySet().size());
        axle.setNbWheels(wheels.size());
        for(int axleId : axleWheels.keySet()) {
            axle.setNbWheelsPerAxle(axleId, axleWheels.get(axleId).size());
            for(Wheel wheel : axleWheels.get(axleId)) {
                axle.setAxleToWheelIds(axleId, wheels.indexOf(wheel));
            }
        }
        for(Wheel wheel : wheels) {
            axle.setWheelIdsInAxleOrder(wheel.getId(), wheel.getId());
        }
    }

    private void initializeResponseParams() {
        PxVehicleBrakeCommandResponseParams brakeResponse = vehicle.getBaseParams().getBrakeResponseParams(0);
        PxVehicleBrakeCommandResponseParams handBrakeResponse = vehicle.getBaseParams().getBrakeResponseParams(1);
        PxVehicleSteerCommandResponseParams steerResponse = vehicle.getBaseParams().getSteerResponseParams();

        brakeResponse.setMaxResponse(maxBrakeResponse);
        handBrakeResponse.setMaxResponse(maxHandBrakeResponse);
        steerResponse.setMaxResponse(maxSteerResponse);

        for(Wheel wheel : wheels) {
            brakeResponse.setWheelResponseMultipliers(wheel.getId(), wheel.getBrakeResponseMultiplier());
            handBrakeResponse.setWheelResponseMultipliers(wheel.getId(), wheel.getHandBrakeResponseMultiplier());
            steerResponse.setWheelResponseMultipliers(wheel.getId(), wheel.getSteeringResponseMultiplier());
        }
    }

    private void setBaseParams(BaseVehicleParams baseParams) {

        //
        // most values taken from Physx/physx/snippets/media/vehicledata/Base.json
        //

        var frame = baseParams.getFrame();
        frame.setLatAxis(PxVehicleAxesEnum.ePosX);
        frame.setLngAxis(PxVehicleAxesEnum.ePosZ);
        frame.setVrtAxis(PxVehicleAxesEnum.ePosY);

        baseParams.getScale().setScale(1f);

        var ackermann = baseParams.getAckermannParams(0);
        ackermann.setWheelIds(0, 0);
        ackermann.setWheelIds(1, 1);
        ackermann.setWheelBase(2.87f);
        ackermann.setTrackWidth(1.6f);
        ackermann.setStrength(1f);

        var suspensionCalc = baseParams.getSuspensionStateCalculationParams();
        suspensionCalc.setSuspensionJounceCalculationType(PxVehicleSuspensionJounceCalculationTypeEnum.eSWEEP);
        suspensionCalc.setLimitSuspensionExpansionVelocity(true);

        var forceAppPoint = new PxVec3(0f, 0f, -0.11f);
        for (int i = 0; i < 4; i++) {
            var suspensionComp = baseParams.getSuspensionComplianceParams(i);
            suspensionComp.getWheelToeAngle().addPair(0f, 0f);
            suspensionComp.getWheelCamberAngle().addPair(0f, 0f);
            suspensionComp.getSuspForceAppPoint().addPair(0f, forceAppPoint);
            suspensionComp.getTireForceAppPoint().addPair(0f, forceAppPoint);
        }

        for (int i = 0; i < 4; i++) {
            var suspensionForce = baseParams.getSuspensionForceParams(i);
            float naturalFrequency = 6f;
            float dampingRatio = 1.8f;

            float sprungMass = mass / 4f;
            float stiffness = naturalFrequency * naturalFrequency * sprungMass;
            float damping = dampingRatio * 2f * (float) Math.sqrt(stiffness * sprungMass);

            suspensionForce.setStiffness(stiffness);
            suspensionForce.setDamping(damping);
            suspensionForce.setSprungMass(sprungMass);
        }

        for (int i = 0; i < 4; i++) {
            var tireForce = baseParams.getTireForceParams(i);
            tireForce.setLongStiff(25_000f);
            tireForce.setLatStiffX(3f);
            tireForce.setLatStiffY(200_000f);
            tireForce.setCamberStiff(0f);
            tireForce.setRestLoad(5500f);
            PxVehicleTireForceParamsExt.setFrictionVsSlip(tireForce, 0, 0, 1.2f);
            PxVehicleTireForceParamsExt.setFrictionVsSlip(tireForce, 0, 1, 1.2f);
            PxVehicleTireForceParamsExt.setFrictionVsSlip(tireForce, 1, 0, 1.1f);
            PxVehicleTireForceParamsExt.setFrictionVsSlip(tireForce, 1, 1, 1.1f);
            PxVehicleTireForceParamsExt.setFrictionVsSlip(tireForce, 2, 0, 0.9f);
            PxVehicleTireForceParamsExt.setFrictionVsSlip(tireForce, 2, 1, 0.9f);

            PxVehicleTireForceParamsExt.setLoadFilter(tireForce, 0, 0, 0f);
            PxVehicleTireForceParamsExt.setLoadFilter(tireForce, 0, 1, 0.23f);
            PxVehicleTireForceParamsExt.setLoadFilter(tireForce, 1, 0, 3f);
            PxVehicleTireForceParamsExt.setLoadFilter(tireForce, 1, 1, 3f);
        }
    }

    private void setPhysxIntegrationParams(PhysXIntegrationParams physxParams, PxVehicleAxleDescription axleDesc, PxGeometry actorGeometry) {
        var filterData = new PxFilterData(0, 0, 0, 0);
        var queryFlags = new PxQueryFlags((short) PxQueryFlagEnum.eSTATIC.value);
        var roadQueryFilterData = new PxQueryFilterData(filterData, queryFlags);

        PxVec3 vec3 = new PxVec3(0f, 0.55f, 1.594f);
        PxQuat quat = new PxQuat(PxIDENTITYEnum.PxIdentity);

        var actorCMassLocalPose = new PxTransform(vec3, quat);

        vec3 = new PxVec3((float) (getVehicleSize().getX() / 2), (float) (getVehicleSize().getY() / 2 - 0.02), (float) getVehicleSize().getZ() / 2);
        quat = new PxQuat(PxIDENTITYEnum.PxIdentity);

        var actorShapeLocalPose = new PxTransform(vec3, quat);

        PxMaterial material = physXManager.getPxPhysics().createMaterial(0.5f, 4.5f, 0.5f);

        PxVehiclePhysXMaterialFriction materialFriction = new PxVehiclePhysXMaterialFriction();
        materialFriction.setFriction(1f);
        materialFriction.setMaterial(material);

        physxParams.create(axleDesc, roadQueryFilterData, null, materialFriction, 1, 1f,
                actorCMassLocalPose, actorGeometry, actorShapeLocalPose, PxVehiclePhysXRoadGeometryQueryTypeEnum.eSWEEP);

    }

    private void setEngineDriveParams(EngineDrivetrainParams engineDriveParams) {

        //
        // most values taken from Physx/physx/snippets/media/vehicledata/EngineDrive.json
        //

        var autobox = engineDriveParams.getAutoboxParams();
        autobox.setUpRatios(0, 0.65f);
        autobox.setUpRatios(1, 0.15f);
        autobox.setUpRatios(2, 0.65f);
        autobox.setUpRatios(3, 0.65f);
        autobox.setUpRatios(4, 0.65f);
        autobox.setUpRatios(5, 0.65f);
        autobox.setUpRatios(6, 0.65f);
        autobox.setDownRatios(0, 0.5f);
        autobox.setDownRatios(1, 0.5f);
        autobox.setDownRatios(2, 0.5f);
        autobox.setDownRatios(3, 0.5f);
        autobox.setDownRatios(4, 0.5f);
        autobox.setDownRatios(5, 0.5f);
        autobox.setDownRatios(6, 0.5f);
        autobox.setLatency(0.5f);

        engineDriveParams.getClutchCommandResponseParams().setMaxResponse(10f);

        engine.init(engineDriveParams.getEngineParams());
        gearbox.init(engineDriveParams.getGearBoxParams());

        var fourWheelDiff = engineDriveParams.getFourWheelDifferentialParams();
        var multiWheelParams = engineDriveParams.getMultiWheelDifferentialParams();
        var tankWheelParams = engineDriveParams.getTankDifferentialParams();
        for (int i = 0; i < 4; i++) {
            fourWheelDiff.setTorqueRatios(i, 0.25f);
            fourWheelDiff.setAveWheelSpeedRatios(i, 0.25f);
            multiWheelParams.setTorqueRatios(i, 0.25f);
            multiWheelParams.setAveWheelSpeedRatios(i, 0.25f);
            tankWheelParams.setTorqueRatios(i, 0.25f);
            tankWheelParams.setAveWheelSpeedRatios(i, 0.25f);
        }
        fourWheelDiff.setFrontWheelIds(0, 0);
        fourWheelDiff.setFrontWheelIds(1, 1);
        fourWheelDiff.setRearWheelIds(0, 2);
        fourWheelDiff.setRearWheelIds(1, 3);
        fourWheelDiff.setCenterBias(1.3f);
        fourWheelDiff.setCenterTarget(1.29f);
        fourWheelDiff.setFrontBias(1.3f);
        fourWheelDiff.setFrontTarget(1.29f);
        fourWheelDiff.setRearBias(1.3f);
        fourWheelDiff.setRearTarget(1.29f);
        fourWheelDiff.setRate(10f);

        var clutch = engineDriveParams.getClutchParams();
        clutch.setAccuracyMode(PxVehicleClutchAccuracyModeEnum.eESTIMATE);
        clutch.setEstimateIterations(5);
    }

    public EngineDriveVehicle getPxVehicle() {
        return this.vehicle;
    }

    public PxVehiclePhysXSimulationContext getContext() {
        return this.context;
    }

    private void destroy() {
        context.destroy();
        vehicle.destroy();
    }

    private static class VehicleComponent extends ObjectComponent {

        protected Vehicle vehicle;

        public VehicleComponent(GameObject object) {
            super(object);
        }

        @Override
        public void onAddComponent() {
            vehicle = (Vehicle) getObject().getComponentByClass(Vehicle.class);
        }

        public Vehicle getVehicle() {
            return this.vehicle;
        }

    }

    @RequiresOneOf({Vehicle.class, Vehicle.TopDown.class})
    @ComponentGL
    @DefaultComponent
    @WebComponent
    @Unique
    public static class Engine extends VehicleComponent {

        private float peakTorque = 600f;
        private float idleRPM = 800f;
        private float maxRPM = 8400f;

        public Engine(GameObject object) {
            super(object);
        }

        @Override
        public void onAddComponent() {
            super.onAddComponent();
            vehicle.setEngine(this);
        }

        public void init(PxVehicleEngineParams engine) {
            engine.getTorqueCurve().addPair(0f, 1f);
            engine.getTorqueCurve().addPair(0.33f, 1f);
            engine.getTorqueCurve().addPair(1f, 1f);
            engine.setMoi(1f);
            engine.setPeakTorque(peakTorque);
            engine.setIdleOmega(VehicleHelper.RPMtoOmega(idleRPM));
            engine.setMaxOmega(VehicleHelper.RPMtoOmega(maxRPM));
            engine.setDampingRateFullThrottle(0.15f);
            engine.setDampingRateZeroThrottleClutchEngaged(2f);
            engine.setDampingRateZeroThrottleClutchDisengaged(0.35f);
        }

        @SaveState(name = "peakTorque")
        public float getPeakTorque() {
            return this.peakTorque;
        }

        @SaveState(name = "idleRPM")
        public float getIdleRPM() {
            return this.idleRPM;
        }

        @SaveState(name = "maxRPM")
        public float getMaxRPM() {
            return this.maxRPM;
        }

        @FromXML
        public void setPeakTorque(float peakTorque) {
            this.peakTorque = peakTorque;
        }

        @FromXML
        public void setIdleRPM(float idleRPM) {
            this.idleRPM = idleRPM;
        }

        @FromXML
        public void setMaxRPM(float maxRPM) {
            this.maxRPM = maxRPM;
        }

    }

    @RequiresOneOf({Vehicle.class, Vehicle.TopDown.class})
    @ComponentGL
    @DefaultComponent
    @WebComponent
    @Unique
    public static class Gearbox extends VehicleComponent {

        private GearboxType gearboxType;
        private List<Float> gearRatios = new ArrayList<>(Arrays.asList(-4f, 0f, 4f, 2f, 1.5f, 1.1f, 1f));
        private int neutralGearIndex = 1;
        private float switchTime = 0.5f;

        private boolean initialized = false;

        public Gearbox(GameObject object) {
            super(object);
        }

        @Override
        public void onAddComponent() {
            super.onAddComponent();
            vehicle.setGearbox(this);
        }

        public void init(PxVehicleGearboxParams gearbox) {
            checkInitialized();
            initialized = true;
            gearbox.setNeutralGear(getNeutralGearIndex());
            for(int i = 0; i < gearRatios.size(); i++) {
                gearbox.setRatios(i, gearRatios.get(i));
            }
            gearbox.setNbRatios(7);
            gearbox.setFinalRatio(4f);
            gearbox.setSwitchTime(0.5f);
        }

        private void checkInitialized() {
            if(initialized) {
                throw new RuntimeException("Gearbox already initialized.");
            }
        }

        @SaveState(name = "gearRatios")
        public String getGearRatiosAsString() {
            List<String> ratios = new ArrayList<>();
            for(Float f : gearRatios) {
                ratios.add(f.toString());
            }
            return String.join(",", ratios);
        }

        public void setGearboxType(GearboxType gearboxType) {
            checkInitialized();
            this.gearboxType = gearboxType;
        }

        @FromXML
        public void setGearboxType(String type) {
            setGearboxType(GearboxType.valueOf(type));
        }

        public void setGearRatios(List<Float> ratios) {
            checkInitialized();
            this.gearRatios = ratios;
        }

        @FromXML
        public void setGearRatios(String ratios) {
            String[] split = ratios.replace(" ", "").split(",");
            List<Float> newRatios = new ArrayList<>();
            for(String s : split) {
                newRatios.add(Float.parseFloat(s));
            }
            setGearRatios(newRatios);
        }

        @FromXML
        public void setNeutralGearIndex(int index) {
            checkInitialized();
            this.neutralGearIndex = index;
        }

        @FromXML
        public void setSwitchTime(float switchTime) {
            this.switchTime = switchTime;
        }

        public void setCurrentGear(int gear) {
            vehicle.getPxVehicle().getEngineDriveState().getGearboxState().setCurrentGear(gear);
        }

        public void setTargetGear(int gear) {
            vehicle.getPxVehicle().getEngineDriveState().getGearboxState().setTargetGear(gear);
        }

        public GearboxType getGearboxType() {
            return this.gearboxType;
        }

        public List<Float> getGearRatios() {
            return new ArrayList<>(this.gearRatios);
        }

        public int getNeutralGearIndex() {
            return this.neutralGearIndex;
        }

        public int getCurrentGear() {
            return vehicle.getPxVehicle().getEngineDriveState().getGearboxState().getCurrentGear();
        }

        public float getSwitchTime() {
            return this.switchTime;
        }

        public enum GearboxType {
            MANUAL,
            AUTOMATIC
        }

    }

    @RequiresOneOf({Vehicle.class, Vehicle.TopDown.class})
    @ComponentGL
    @DefaultComponent
    @WebComponent
    public static class Wheel extends VehicleComponent {

        private int id = -1;
        private int axleId = 0;
        private float brakeResponseMultiplier = 1;
        private float handBrakeResponseMultiplier = 0;
        private float steeringResponseMultiplier = 0;
        private float radius = 3.5f;
        private float mass = 12f;
        private float dampingRate = 0.5f;
        private float width = 0.6f;
        private TransformSet localPosition = new TransformSet();
        private float suspensionTravelDistance = 0.3f;

        private RenderOptions3D options;

        public Wheel(GameObject object) {
            super(object);
        }

        @Override
        public void onAddComponent() {
            super.onAddComponent();
            vehicle.registerWheel(this);
        }

        @Override
        public void onRemoveComponent() {
            if(getScene().getPanel() instanceof PanelGL) {
                GraphicsManager3D g = ((PanelGL) getScene().getPanel()).getGraphicsManager3D();
                g.removeRenderable(options.getIdentifier());
            }
        }

        @Override
        public void onPhysicsUpdate() {
            if(id == -1 || options == null) {
                return;
            }
            PxTransform wheelLocalPose = vehicle.getPxVehicle().getBaseState().getWheelLocalPoses(id).getLocalPose();
            QuaternionTransformSet newRotation =
                    getObject().getQuaternionRotation().clone().multiply(QuaternionTransformSet.fromPhysX(wheelLocalPose.getQ()));
            options.setQuaternionRotation(newRotation);

            TransformSet wheelLocalPos = new TransformSet().fromPhysX(wheelLocalPose.getP());
            TransformSet wheelPosition = RotationHelper.multiplyWithQuaternion(wheelLocalPos, getObject().getQuaternionRotation());
            options.setPosition(wheelPosition.add(getObject().getPosition()));
        }

        public void init() {
            if(id == -1) {
                throw new RuntimeException("Wheel cannot be initialized, because wheel ID is not set.");
            }
            BaseVehicleParams params = vehicle.getPxVehicle().getBaseParams();
            initWheelParams(params.getWheelParams(id));
            initSuspensionParams(params.getSuspensionParams(id));
            createRenderable();
        }

        private void initWheelParams(PxVehicleWheelParams params) {
            params.setMass(mass);
            params.setRadius(radius);
            params.setHalfWidth(width / 2);
            params.setDampingRate(dampingRate);
            params.setMoi(0.5f * mass * radius * radius);
        }

        private void initSuspensionParams(PxVehicleSuspensionParams params) {
            PxVec3 travelDirection = new PxVec3(0, -1, 0);
            params.setSuspensionTravelDir(travelDirection);
            //
            PxVec3 suspensionAttachment = new PxVec3((float) localPosition.getX(), (float) localPosition.getY(), (float) localPosition.getZ());
            PxQuat suspensionAttachmentRotation = new PxQuat(PxIDENTITYEnum.PxIdentity);
            PxTransform suspensionAttachmentTransform = new PxTransform(suspensionAttachment, suspensionAttachmentRotation);
            params.setSuspensionAttachment(suspensionAttachmentTransform);
            //
            PxVec3 wheelAttachment = new PxVec3(0, 0, 0);
            PxQuat wheelAttachmentRotation = new PxQuat(PxIDENTITYEnum.PxIdentity);
            PxTransform wheelAttachmentTransform = new PxTransform(wheelAttachment, wheelAttachmentRotation);
            params.setWheelAttachment(wheelAttachmentTransform);
            //
            params.setSuspensionTravelDist(suspensionTravelDistance);
            //
            travelDirection.destroy();
            //
            suspensionAttachmentTransform.destroy();
            suspensionAttachment.destroy();
            suspensionAttachmentRotation.destroy();
            //
            wheelAttachmentTransform.destroy();
            wheelAttachment.destroy();
            wheelAttachmentRotation.destroy();
        }

        private void createRenderable() {
            if(getScene().getPanel() instanceof PanelGL) {
                GraphicsManager3D g = ((PanelGL) getScene().getPanel()).getGraphicsManager3D();
                options = new RenderOptions3D(getObject().getIdentifier() + "$WHEEL-" + id)
                        .setPosition(getObject().getPosition())
                        .setSize(new TransformSet(width, radius, radius))
                        .setRotation(getObject().getRotation())
                        .setQuaternionRotation(new QuaternionTransformSet())
                        .setShader("shaders/shader")
                        .setShapePath("models/box.obj");
                g.createRenderable(options);
            }
        }

        @SaveState(name = "id")
        public int getId() {
            return this.id;
        }

        @FromXML
        public void setId(int id) {
            this.id = id;
        }

        @SaveState(name = "axleId")
        public int getAxleId() {
            return this.axleId;
        }

        @FromXML
        public void setAxleId(int axleId) {
            this.axleId = axleId;
        }

        @SaveState(name = "brakeResponseMultiplier")
        public float getBrakeResponseMultiplier() {
            return this.brakeResponseMultiplier;
        }

        @FromXML
        public void setBrakeResponseMultiplier(float brakeResponseMultiplier) {
            this.brakeResponseMultiplier = brakeResponseMultiplier;
        }

        @SaveState(name = "handBrakeResponseMultiplier")
        public float getHandBrakeResponseMultiplier() {
            return this.handBrakeResponseMultiplier;
        }

        @FromXML
        public void setHandBrakeResponseMultiplier(float handBrakeResponseMultiplier) {
            this.handBrakeResponseMultiplier = handBrakeResponseMultiplier;
        }

        @SaveState(name = "steeringResponseMultiplier")
        public float getSteeringResponseMultiplier() {
            return this.steeringResponseMultiplier;
        }

        @FromXML
        public void setSteeringResponseMultiplier(float steeringResponseMultiplier) {
            this.steeringResponseMultiplier = steeringResponseMultiplier;
        }

        @SaveState(name = "radius")
        public float getRadius() {
            return this.radius;
        }

        @FromXML
        public void setRadius(float radius) {
            this.radius = radius;
        }

        @SaveState(name = "mass")
        public float getMass() {
            return this.mass;
        }

        @FromXML
        public void setMass(float mass) {
            this.mass = mass;
        }

        @SaveState(name = "dampingRate")
        public float getDampingRate() {
            return this.dampingRate;
        }

        @FromXML
        public void setDampingRate(float dampingRate) {
            this.dampingRate = dampingRate;
        }

        @SaveState(name = "width")
        public float getWidth() {
            return this.width;
        }

        @FromXML
        public void setWidth(float width) {
            this.width = width;
        }

        @SaveState(name = "localPosition")
        public TransformSet getLocalPosition() {
            return this.localPosition;
        }

        @FromXML
        public void setLocalPosition(TransformSet localPosition) {
            this.localPosition = localPosition;
        }

        @SaveState(name = "suspensionTravelDistance")
        public float getSuspensionTravelDistance() {
            return this.suspensionTravelDistance;
        }

        @FromXML
        public void setSuspensionTravelDistance(float distance) {
            this.suspensionTravelDistance = distance;
        }

    }

    protected void updateWorldPositions() {
        PxVec3 vec3 = vehicle.getPhysXState().getPhysxActor().getRigidBody().getGlobalPose().getP();
        PxQuat rotation = vehicle.getPhysXState().getPhysxActor().getRigidBody().getGlobalPose().getQ();
        getObject().getPosition().set(vec3.getX(), vec3.getY() + getObject().getSizeY() * 1.5 + 1.35, vec3.getZ());
        getObject().setQuaternionRotation(new QuaternionTransformSet(rotation.getX(), rotation.getY(), rotation.getZ(), rotation.getW()));
    }

    // Events

    @Override
    public void onRemoveComponent() {
        destroy();
    }

    @Override
    public void onSerializationFinish() {
        initialize();
    }

    @Override
    public void onPhysicsUpdate() {
        if(vehicle.getPhysXState().getPhysxActor().getRigidBody() == null) {
            return;
        }
        updateWorldPositions();
    }

    @DefaultComponent
    @WebComponent
    public static class TopDown extends Vehicle {

        public TopDown(GameObject object) {
            super(object);
        }

        @Override
        protected void updateWorldPositions() {
            PxVec3 vec3 = vehicle.getPhysXState().getPhysxActor().getRigidBody().getGlobalPose().getP();
            PxQuat rotation = vehicle.getPhysXState().getPhysxActor().getRigidBody().getGlobalPose().getQ();
            getObject().setPosition(new TransformSet(vec3.getX(), vec3.getZ(), 10));
            getObject().setRotation(new TransformSet(RotationHelper.quaternionToYaw(rotation.getY(), rotation.getW()), 0, 0));
        }

        protected PxGeometry createGeometry() {
            return new PxBoxGeometry(
                    (float) getObject().getSize().getX(),
                    (float) 5 / 2,
                    (float) getObject().getSize().getY()
            );
        }

        protected TransformSet getVehicleSize() {
            return new TransformSet(getObject().getSize().getX(), (double) 5 / 2, getObject().getSize().getY());
        }

        @Override
        protected TransformSet getVehicleInitializationPose() {
            return new TransformSet(getObject().getX(), 10, getObject().getY());
        }

    }

}
