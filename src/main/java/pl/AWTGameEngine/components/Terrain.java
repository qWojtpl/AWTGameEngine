package pl.AWTGameEngine.components;

import org.lwjgl.system.MemoryStack;
import physx.PxTopLevelFunctions;
import physx.common.*;
import physx.extensions.PxRigidActorExt;
import physx.geometry.*;
import physx.physics.*;
import physx.support.PxArray_PxHeightFieldSample;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.PhysXManager;
import pl.AWTGameEngine.engine.graphics.GraphicsManagerGL;
import pl.AWTGameEngine.engine.helpers.HeightFieldHelper;
import pl.AWTGameEngine.engine.helpers.ModelHelper;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.transform.QuaternionTransformSet;
import pl.AWTGameEngine.objects.transform.Vector3;

import java.util.ArrayList;
import java.util.List;

@ComponentGL
public class Terrain extends ObjectComponent {

    private final List<Short> heights = new ArrayList<>();
    private int cols = 640;
    private int rows = 640;
    private RenderOptions3D renderOptions3D;
    private PxRigidActor actor;

    public Terrain(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        Logger.info("Loading terrain...");
        try(MemoryStack stack = MemoryStack.stackPush()) {

            PhysXManager.PhysXScene physXScene = PhysXManager.getInstance().getScene(getScene());

            PxTransform pose = PxTransform.createAt(stack, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
            pose.getP().setX((float) getObject().getPosition().getX());
            pose.getP().setY((float) getObject().getPosition().getY());
            pose.getP().setZ((float) getObject().getPosition().getZ());
            pose.getQ().setX((float) getObject().getQuaternionRotation().getX());
            pose.getQ().setY((float) getObject().getQuaternionRotation().getY());
            pose.getQ().setZ((float) getObject().getQuaternionRotation().getZ());
            pose.getQ().setW((float) getObject().getQuaternionRotation().getW());
            actor = PhysXManager.getInstance().getPxPhysics().createRigidStatic(pose);

            PxArray_PxHeightFieldSample samples = PxArray_PxHeightFieldSample.createAt(stack, MemoryStack::nmalloc, rows * cols);
            PxHeightFieldSample sample = PxHeightFieldSample.createAt(stack, MemoryStack::nmalloc);
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    short h = (short) (Math.sin(row + col) * 1.25 * Math.cos(row));
/*                    if(col + row == 64) {
                        h = 40 * 100;
                    }*/
                    sample.setHeight(h);
                    samples.set(col * rows + row, sample);
                    heights.add(h);
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
            geometry.setHeightScale(5);
            geometry.setHeightField(heightField);

            PxMeshGeometryFlags geometryFlags = PxMeshGeometryFlags.createAt(stack, MemoryStack::nmalloc, (byte) 0);
            geometry.setHeightFieldFlags(geometryFlags);

            PxRigidActorExt.createExclusiveShape(actor, geometry, PhysXManager.getInstance().getDefaultMaterial(), PhysXManager.getInstance().getShapeFlags());

            physXScene.getPxScene().addActor(actor);

            createRenderable(geometry.getRowScale(), geometry.getColumnScale(), geometry.getHeightScale());
        }
    }

    private void createRenderable(float rowScale, float columnScale, float heightScale) {
        List<float[]> vertices = HeightFieldHelper.generateHeightFieldVertices(this::getInvertedHeight, rows, cols, rowScale, columnScale, heightScale);

        GraphicsManagerGL graphicsManagerGL = (GraphicsManagerGL) ((PanelGL) getScene().getPanel()).getGraphicsManager3D();
        String identifier = "$terrain-" + getObject().getIdentifier();
        graphicsManagerGL.addPreloadedVertices(identifier, ModelHelper.convertToArray(vertices));
        renderOptions3D = new RenderOptions3D(identifier)
                .setPosition(getObject().getPosition())
                .setSize(new Vector3(1, 1, 1))
                .setQuaternionRotation(new QuaternionTransformSet())
                .setShapePath(identifier)
                .setSprite(Dependencies.getResourceManager().getResourceAsSprite("hdr_sprites/grass.jpg"))
                .setRepeatTexture(160)
                .setShader("shaders/shader");

        graphicsManagerGL.createRenderable(renderOptions3D);
    }

    public short getHeight(int row, int col) {
        return heights.get(col * rows + row);
    }

    public short getInvertedHeight(int row, int col) {
        return heights.get(row * cols + col);
    }

    @SaveState(name = "rows")
    public int getRows() {
        return this.rows;
    }

    @FromXML
    public void setRows(int rows) {
        if(rows <= 0) {
            rows = 1;
        }
        this.rows = rows;
    }

    @SaveState(name = "columns")
    public int getColumns() {
        return this.cols;
    }

    @FromXML
    public void setColumns(int cols) {
        if(cols <= 0) {
            cols = 1;
        }
        this.cols = cols;
    }

}
