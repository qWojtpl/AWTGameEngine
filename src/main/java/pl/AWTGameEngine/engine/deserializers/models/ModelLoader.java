package pl.AWTGameEngine.engine.deserializers.models;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.helpers.ModelHelper;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelLoader {

    public static float[] getVertices(String path, boolean center) {
        Logger.info("Loading 3D model: " + path + (center ? " (with center correction)" : "") + "...");
        String[] split = path.split("\\.");
        String type = split[split.length - 1];
        if(type.equals("o3d")) {
            return O3DLoader.getVertices(path, center);
        }
        ByteBuffer buffer = Dependencies.getResourceManager().getResourceAsByteBuffer(path);
        try(AIScene scene = Assimp.aiImportFileFromMemory(
                buffer,
                Assimp.aiProcess_Triangulate | Assimp.aiProcess_GenNormals,
                type)) {
            assert scene != null;
            PointerBuffer meshes = scene.mMeshes();
            if(meshes == null) {
                throw new RuntimeException("Meshes not found.");
            }
            List<float[]> vertices = new ArrayList<>();
            for(int i = 0; i < scene.mNumMeshes(); i++) {
                AIMesh mesh = AIMesh.create(meshes.get(i));
                for(int j = 0; j < mesh.mNumVertices(); j++) {
                    float[] seg = new float[8];
                    AIVector3D pos = mesh.mVertices().get(j);
                    seg[0] = pos.x();
                    seg[1] = pos.y();
                    seg[2] = pos.z();
                    AIVector3D normal = mesh.mNormals().get(j);
                    seg[3] = normal.x();
                    seg[4] = normal.y();
                    seg[5] = normal.z();
                    if(mesh.mNumUVComponents(0) > 0) {
                        AIVector3D uv = mesh.mTextureCoords(0).get(j);
                        seg[6] = uv.x();
                        seg[7] = 1.0f - uv.y();
                    } else {
                        seg[6] = 0f;
                        seg[7] = 0f;
                    }
                    vertices.add(seg);
                }
                mesh.close();
            }
            if(center) {
                ModelHelper.centerCorrection(vertices);
            }

            return ModelHelper.convertToArray(vertices);
        } catch(Exception e) {
            Logger.exception("Cannot get vertices from " + path, e);
            return new float[0];
        }
    }

}
