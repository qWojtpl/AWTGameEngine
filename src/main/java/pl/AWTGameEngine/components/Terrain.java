package pl.AWTGameEngine.components;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physx.PxTopLevelFunctions;
import physx.common.*;
import physx.extensions.PxExtensionTopLevelFunctions;
import physx.extensions.PxRigidActorExt;
import physx.geometry.*;
import physx.physics.*;
import physx.support.PxArray_PxHeightFieldSample;
import physx.vehicle2.PxVehicleTopLevelFunctions;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.deserializers.models.ModelLoader;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.helpers.HeightFieldHelper;
import pl.AWTGameEngine.engine.helpers.ModelHelper;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.transform.QuaternionTransformSet;
import pl.AWTGameEngine.objects.transform.TransformSet;

import java.util.ArrayList;
import java.util.List;

@ComponentGL
public class Terrain extends ObjectComponent {

    private List<Short> height = new ArrayList<>();

    public Terrain(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        try(MemoryStack stack = MemoryStack.stackPush()) {

            PhysXManager.PhysXScene physXScene = PhysXManager.getInstance().getScene(getScene());

            PxTransform identityPose = PxTransform.createAt(stack, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
            PxRigidActor actor = PhysXManager.getInstance().getPxPhysics().createRigidStatic(identityPose);

            int rows = 64;
            int cols = 64;

            PxArray_PxHeightFieldSample samples = PxArray_PxHeightFieldSample.createAt(stack, MemoryStack::nmalloc, rows * cols);
            PxHeightFieldSample sample = PxHeightFieldSample.createAt(stack, MemoryStack::nmalloc);
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    short h = (short) (Math.sin(row + col) * 1.25);
                    if(col + row == 64) {
                        h = 40;
                    }
                    sample.setHeight(h);
                    samples.set(col * rows + row, sample);
                    height.add(h);
                }
            }

            PxStridedData stridedData = PxStridedData.wrapPointer(samples.getAddress());
            stridedData.setStride(PxHeightFieldSample.SIZEOF);

            PxHeightFieldDesc desc = PxHeightFieldDesc.createAt(stack, MemoryStack::nmalloc);
            desc.setFormat(PxHeightFieldFormatEnum.eS16_TM);
            desc.setNbColumns(cols);
            desc.setNbRows(rows);
            desc.setSamples(stridedData);
            PxHeightFieldFlags flags = PxHeightFieldFlags.createAt(stack, MemoryStack::nmalloc, (short) 0);
            desc.setFlags(flags);

            PxHeightField heightField = PxTopLevelFunctions.CreateHeightField(desc);
            PxHeightFieldGeometry geometry = PxHeightFieldGeometry.createAt(stack, MemoryStack::nmalloc);
            geometry.setColumnScale(10);
            geometry.setRowScale(10);
            geometry.setHeightScale(1);
            geometry.setHeightField(heightField);

            PxMeshGeometryFlags geometryFlags = PxMeshGeometryFlags.createAt(stack, MemoryStack::nmalloc, (byte) 0);
            geometry.setHeightFieldFlags(geometryFlags);

            PxRigidActorExt.createExclusiveShape(actor, geometry, PhysXManager.getInstance().getDefaultMaterial(), PhysXManager.getInstance().getShapeFlags());

            physXScene.getPxScene().addActor(actor);

            List<float[]> vertices = HeightFieldHelper.generateHeightFieldVertices(this::getHeight, rows, cols, geometry.getRowScale(), geometry.getColumnScale(), geometry.getHeightScale());

            GraphicsManagerGL graphicsManagerGL = (GraphicsManagerGL) ((PanelGL) getScene().getPanel()).getGraphicsManager3D();
            String identifier = "$terrain-" + getObject().getIdentifier();
            graphicsManagerGL.addPreloadedVertices(identifier, ModelHelper.convertToArray(vertices));
            RenderOptions3D renderOptions3D = new RenderOptions3D(getObject().getIdentifier() + "$terrain")
                    .setPosition(new TransformSet(0, 0, 0))
                    .setSize(new TransformSet(1, 1, 1))
                    .setQuaternionRotation(new QuaternionTransformSet())
                    .setShapePath(identifier)
                    .setShader("shaders/shader");
            graphicsManagerGL.createRenderable(renderOptions3D);
        }
    }

    public short getHeight(int row, int col) {
        return height.get(row * 64 + col);
    }
    
}
