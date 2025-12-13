package pl.AWTGameEngine.components;

import physx.common.PxIDENTITYEnum;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.cooking.PxCookingParams;
import physx.geometry.PxBoxGeometry;
import physx.geometry.PxGeometry;
import physx.physics.*;
import physx.vehicle2.*;
import pl.AWTGameEngine.annotations.*;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.QuaternionTransformSet;
import pl.AWTGameEngine.objects.TransformSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@ComponentFX
@ComponentGL
@Unique
public class Vehicle extends ObjectComponent {

    private final PhysXManager physXManager = PhysXManager.getInstance();
    private final EngineDriveVehicle vehicle = new EngineDriveVehicle();
    private final PxVehiclePhysXSimulationContext context = new PxVehiclePhysXSimulationContext();
    private final List<Wheel> wheels = new ArrayList<>();

    private Gearbox gearbox;

    public Vehicle(GameObject object) {
        super(object);
    }

    private void setGearbox(Gearbox gearbox) {
        this.gearbox = gearbox;
    }

    public Gearbox getGearbox() {
        return this.gearbox;
    }

    private void initialize() {
        PxGeometry geometry = new PxBoxGeometry(
                (float) getObject().getSize().getX(),
                (float) getObject().getSize().getY(),
                (float) getObject().getSize().getZ());
//        initializeBaseParams(vehicle.getBaseParams());

        setBaseParams(vehicle.getBaseParams());
        setPhysxIntegrationParams(vehicle.getPhysXParams(), vehicle.getBaseParams().getAxleDescription(), geometry);
        setEngineDriveParams(vehicle.getEngineDriveParams(), vehicle.getBaseParams().getAxleDescription());

        vehicle.initialize(
                physXManager.getPxPhysics(),
                physXManager.getCookingParams(),
                physXManager.getPxPhysics().createMaterial(0.5f, 0.5f, 0.5f),
                EngineDriveVehicleEnum.eDIFFTYPE_MULTIWHEELDRIVE
        );

        var vehiclePose = new PxTransform(
                new PxVec3((float) getObject().getPosition().getX(), (float) getObject().getPosition().getY(), (float) getObject().getPosition().getZ()),
                new PxQuat(PxIDENTITYEnum.PxIdentity));

        vehicle.getPhysXState().getPhysxActor().getRigidBody().setGlobalPose(vehiclePose);
        physXManager.getPxScene().addActor(vehicle.getPhysXState().getPhysxActor().getRigidBody());

        vehicle.getEngineDriveState().getGearboxState().setCurrentGear(vehicle.getEngineDriveParams().getGearBoxParams().getNeutralGear() + 1);
        vehicle.getEngineDriveState().getGearboxState().setTargetGear(vehicle.getEngineDriveParams().getGearBoxParams().getNeutralGear() + 1);

        context.setToDefault();
        context.getFrame().setLngAxis(PxVehicleAxesEnum.ePosZ);
        context.getFrame().setLatAxis(PxVehicleAxesEnum.ePosX);
        context.getFrame().setVrtAxis(PxVehicleAxesEnum.ePosY);
        context.getScale().setScale(1f);
        context.setGravity(physXManager.getPxScene().getGravity());
        context.setPhysxScene(physXManager.getPxScene());
        context.setPhysxActorUpdateMode(PxVehiclePhysXActorUpdateModeEnum.eAPPLY_ACCELERATION);
        context.setPhysxUnitCylinderSweepMesh(
                PxVehicleTopLevelFunctions.VehicleUnitCylinderSweepMeshCreate(context.getFrame(), physXManager.getPxPhysics(), physXManager.getCookingParams()));


        physXManager.registerVehicle(this);
        vehicle.getPhysXState().getPhysxActor().getRigidBody().setGlobalPose(new PxTransform(new PxVec3(0, 50, 0)));
        vehicle.getCommandState().setThrottle(0.1f);
        vehicle.getCommandState().setNbBrakes(1);
        vehicle.getCommandState().setSteer(0.3f);

    }

    private void initializeBaseParams(BaseVehicleParams params) {



    }

    private void setBaseParams(BaseVehicleParams baseParams) {

        //
        // most values taken from Physx/physx/snippets/media/vehicledata/Base.json
        //

        var axleDesc = baseParams.getAxleDescription();
        axleDesc.setNbAxles(2);
        axleDesc.setNbWheels(4);
        axleDesc.setNbWheelsPerAxle(0, 2);
        axleDesc.setNbWheelsPerAxle(1, 2);
        axleDesc.setAxleToWheelIds(0, 0);
        axleDesc.setAxleToWheelIds(1, 2);
        for (int i = 0; i < 4; i++) {
            axleDesc.setWheelIdsInAxleOrder(i, i);
        }

        var frame = baseParams.getFrame();
        frame.setLatAxis(PxVehicleAxesEnum.ePosX);
        frame.setLngAxis(PxVehicleAxesEnum.ePosZ);
        frame.setVrtAxis(PxVehicleAxesEnum.ePosY);

        baseParams.getScale().setScale(1f);

        var rigidBody = baseParams.getRigidBodyParams();
        float mass = 870;
        rigidBody.setMass(mass);
        double width  = getObject().getSize().getX();
        double height = getObject().getSize().getY();
        double length = getObject().getSize().getZ();

        float moiX = (float)((1/12.0) * mass * (height*height + length*length));
        float moiY = (float)((1/12.0) * mass * (width*width + length*length));
        float moiZ = (float)((1/12.0) * mass * (width*width + height*height));
        PxVec3 moi = new PxVec3(moiX, moiY, moiZ);
        rigidBody.setMoi(moi);

        var brakeResponse = baseParams.getBrakeResponseParams(0);
        brakeResponse.setMaxResponse(2000f);
        for (int i = 0; i < 4; i++) {
            brakeResponse.setWheelResponseMultipliers(i, 1f);
        }

        var handBrakeResponse = baseParams.getBrakeResponseParams(0);
        handBrakeResponse.setMaxResponse(2000f);
        for (int i = 0; i < 4; i++) {
            handBrakeResponse.setWheelResponseMultipliers(i, i < 2 ? 1f : 0f);
        }

        var steerResponse = baseParams.getSteerResponseParams();
        steerResponse.setMaxResponse(0.5f);
        for (int i = 0; i < 4; i++) {
            steerResponse.setWheelResponseMultipliers(i, i < 2 ? 1f : 0f);
        }

        var ackermann = baseParams.getAckermannParams(0);
        ackermann.setWheelIds(0, 0);
        ackermann.setWheelIds(1, 1);
        ackermann.setWheelBase(2.87f);
        ackermann.setTrackWidth(1.6f);
        ackermann.setStrength(1f);

        List<PxTransform> suspensionAttachmentPoses = new ArrayList<>();

        float halfWidth  = (float) getObject().getSize().getX() / 2;
        float halfHeight  = (float) getObject().getSize().getY() / 2;
        float halfLength = (float) getObject().getSize().getZ() / 2;
        float wheelRadius = 0.35f;
        float yPos = halfHeight + wheelRadius;

        PxVec3 pos = new PxVec3(-halfWidth, yPos, halfLength);
        PxQuat quat = new PxQuat(PxIDENTITYEnum.PxIdentity);

        PxTransform transform = new PxTransform(pos, quat);

        suspensionAttachmentPoses.add(transform);

        pos = new PxVec3(halfWidth, yPos, halfLength);
        quat = new PxQuat(PxIDENTITYEnum.PxIdentity);

        transform = new PxTransform(pos, quat);

        suspensionAttachmentPoses.add(transform);

        pos = new PxVec3(-halfWidth, yPos, -halfLength);
        quat = new PxQuat(PxIDENTITYEnum.PxIdentity);

        transform = new PxTransform(pos, quat);

        suspensionAttachmentPoses.add(transform);

        pos = new PxVec3(halfWidth, yPos, -halfLength);
        quat = new PxQuat(PxIDENTITYEnum.PxIdentity);

        transform = new PxTransform(pos, quat);

        suspensionAttachmentPoses.add(transform);

        pos = new PxVec3(0f, 0f, 0f);
        quat = new PxQuat(PxIDENTITYEnum.PxIdentity);

        var wheelAttachmentPose = new PxTransform(
                pos,
                quat
        );

        var suspensionTravelDir = new PxVec3(0f, -1f, 0f);
        for (int i = 0; i < 4; i++) {
            var suspension = baseParams.getSuspensionParams(i);
            suspension.setSuspensionAttachment(suspensionAttachmentPoses.get(i));
            suspension.setSuspensionTravelDir(suspensionTravelDir);
            suspension.setWheelAttachment(wheelAttachmentPose);
            suspension.setSuspensionTravelDist(1f/*(float) (getObject().getSizeY() + wheelRadius)*/);
        }

        var suspensionCalc = baseParams.getSuspensionStateCalculationParams();
        suspensionCalc.setSuspensionJounceCalculationType(PxVehicleSuspensionJounceCalculationTypeEnum.eSWEEP);
        suspensionCalc.setLimitSuspensionExpansionVelocity(false);

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
            float naturalFrequency = 7f;
            float dampingRatio = 1.0f;

            float sprungMass = mass / 4f;
            float stiffness = naturalFrequency * naturalFrequency * sprungMass;
            float damping   = dampingRatio * 2f * (float) Math.sqrt(stiffness * sprungMass);

            suspensionForce.setStiffness(stiffness);
            suspensionForce.setDamping(damping);
            suspensionForce.setSprungMass(sprungMass);
        }

        for (int i = 0; i < 4; i++) {
            var tireForce = baseParams.getTireForceParams(i);
            tireForce.setLongStiff(25_000f);
            tireForce.setLatStiffX(0.01f);
            tireForce.setLatStiffY(120_000f);
            tireForce.setCamberStiff(0f);
            tireForce.setRestLoad(5500f);
            PxVehicleTireForceParamsExt.setFrictionVsSlip(tireForce, 0, 0, 0f);
            PxVehicleTireForceParamsExt.setFrictionVsSlip(tireForce, 0, 1, 1f);
            PxVehicleTireForceParamsExt.setFrictionVsSlip(tireForce, 1, 0, 0.1f);
            PxVehicleTireForceParamsExt.setFrictionVsSlip(tireForce, 1, 1, 1f);
            PxVehicleTireForceParamsExt.setFrictionVsSlip(tireForce, 2, 0, 1f);
            PxVehicleTireForceParamsExt.setFrictionVsSlip(tireForce, 2, 1, 1f);

            PxVehicleTireForceParamsExt.setLoadFilter(tireForce, 0, 0, 0f);
            PxVehicleTireForceParamsExt.setLoadFilter(tireForce, 0, 1, 0.23f);
            PxVehicleTireForceParamsExt.setLoadFilter(tireForce, 1, 0, 3f);
            PxVehicleTireForceParamsExt.setLoadFilter(tireForce, 1, 1, 3f);
        }

        float wheelMass = 12f;

        for (int i = 0; i < 4; i++) {
            var wheel = baseParams.getWheelParams(i);
            wheel.setMass(wheelMass);
            wheel.setRadius(wheelRadius);
            wheel.setHalfWidth(0.3f);
            wheel.setDampingRate(0.5f);
            wheel.setMoi(0.5f * wheelMass * wheelRadius * wheelRadius);
        }
    }

    private void setPhysxIntegrationParams(PhysXIntegrationParams physxParams, PxVehicleAxleDescription axleDesc, PxGeometry actorGeometry) {
        var filterData = new PxFilterData(0, 0, 0, 0);
        var queryFlags = new PxQueryFlags((short) PxQueryFlagEnum.eSTATIC.value);
        var roadQueryFilterData = new PxQueryFilterData(filterData, queryFlags);

        PxVec3 vec3 = new PxVec3(0f, 0.55f, 1.594f);
        PxQuat quat = new PxQuat(PxIDENTITYEnum.PxIdentity);

        var actorCMassLocalPose = new PxTransform(vec3, quat);

        vec3 = new PxVec3((float) (getObject().getSize().getX() / 2), (float) (getObject().getSize().getY() / 2 - 0.02), (float) getObject().getSize().getZ() / 2);
        quat = new PxQuat(PxIDENTITYEnum.PxIdentity);

        var actorShapeLocalPose = new PxTransform(vec3, quat);

        PxMaterial material = physXManager.getPxPhysics().createMaterial(0.5f, 0.5f, 0.5f);

        PxVehiclePhysXMaterialFriction materialFriction = new PxVehiclePhysXMaterialFriction();
        materialFriction.setFriction(1f);
        materialFriction.setMaterial(material);

        physxParams.create(axleDesc, roadQueryFilterData, null, materialFriction, 1, 1f,
                actorCMassLocalPose, actorGeometry, actorShapeLocalPose, PxVehiclePhysXRoadGeometryQueryTypeEnum.eSWEEP);

    }

    private void setEngineDriveParams(EngineDrivetrainParams engineDriveParams, PxVehicleAxleDescription axleDesc) {

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
        autobox.setLatency(2f);

        engineDriveParams.getClutchCommandResponseParams().setMaxResponse(10f);

        var engine = engineDriveParams.getEngineParams();
        engine.getTorqueCurve().addPair(0f, 1f);
        engine.getTorqueCurve().addPair(0.33f, 1f);
        engine.getTorqueCurve().addPair(1f, 1f);
        engine.setMoi(1f);
        engine.setPeakTorque(500f);
        engine.setIdleOmega(0);
        engine.setMaxOmega(600f);
        engine.setDampingRateFullThrottle(0.15f);
        engine.setDampingRateZeroThrottleClutchEngaged(2f);
        engine.setDampingRateZeroThrottleClutchDisengaged(0.35f);

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

    public void initializeWheel(Wheel wheel) {
        wheels.add(wheel);
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

    @Requires(Vehicle.class)
    @ComponentGL
    @ComponentFX
    public static class Wheel extends ObjectComponent {

        private Vehicle vehicle;

        public Wheel(GameObject object) {
            super(object);
        }

        @Override
        public void onAddComponent() {
            vehicle = (Vehicle) getObject().getComponentByClass(Vehicle.class);
            vehicle.initializeWheel(this);
        }

        public Vehicle getVehicle() {
            return this.vehicle;
        }

    }

    @Requires(Vehicle.class)
    @ComponentGL
    @ComponentFX
    public static class Gearbox extends ObjectComponent {

        private Vehicle vehicle;
        private GearboxType gearboxType;
        private List<Float> gearRatios = new ArrayList<>(Arrays.asList(-4f, 0f, 4f, 2f, 1.5f, 1.1f, 1f));
        private int neutralGearIndex = 1;
        private float switchTime;

        private boolean initialized = false;

        public Gearbox(GameObject object) {
            super(object);
        }

        @Override
        public void onAddComponent() {
            vehicle = (Vehicle) getObject().getComponentByClass(Vehicle.class);
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

        public void setNeutralGearIndex(int index) {
            checkInitialized();
            this.neutralGearIndex = index;
        }

        @FromXML
        public void setNeutralGearIndex(String index) {
            setNeutralGearIndex(Integer.parseInt(index));
        }

        public void setCurrentGear(int gear) {
            vehicle.getPxVehicle().getEngineDriveState().getGearboxState().setCurrentGear(gear);
        }

        public void setTargetGear(int gear) {
            vehicle.getPxVehicle().getEngineDriveState().getGearboxState().setTargetGear(gear);
        }

        public Vehicle getVehicle() {
            return this.vehicle;
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

        public enum GearboxType {
            MANUAL,
            AUTOMATIC
        }

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
        PxVec3 vec3 = vehicle.getPhysXState().getPhysxActor().getRigidBody().getGlobalPose().getP();
        PxQuat rotation = vehicle.getPhysXState().getPhysxActor().getRigidBody().getGlobalPose().getQ();
        getObject().setPosition(new TransformSet(vec3.getX(), vec3.getY(), vec3.getZ()));
        getObject().setQuaternionRotation(new QuaternionTransformSet(rotation.getX(), rotation.getY(), rotation.getZ(), rotation.getW()));
    }

}
