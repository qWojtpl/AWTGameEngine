package pl.AWTGameEngine.engine.deserializers;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.Logger;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ModelLoader {

    public static float[] getVertices(String path, boolean center) {
        Logger.info("Loading 3D model: " + path + (center ? " (with center correction)" : ""));
        ByteBuffer buffer = Dependencies.getResourceManager().getResourceAsByteBuffer(path);
        String[] split = path.split("\\.");
        String type = split[split.length - 1];
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
                float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY, minZ = Float.POSITIVE_INFINITY;
                float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY, maxZ = Float.NEGATIVE_INFINITY;
                for(float[] seg : vertices) {
                    if(seg[0] < minX) {
                        minX = seg[0];
                    }
                    if(seg[0] > maxX) {
                        maxX = seg[0];
                    }
                    if(seg[1] < minY) {
                        minY = seg[1];
                    }
                    if(seg[1] > maxY) {
                        maxY = seg[1];
                    }
                    if(seg[2] < minZ) {
                        minZ = seg[2];
                    }
                    if(seg[2] > maxZ) {
                        maxZ = seg[2];
                    }
                }
                float centerX = ((minX + maxX) / 2), centerY = ((minY + maxY) / 2), centerZ = ((minZ + maxZ) / 2);
                for(float[] seg : vertices) {
                    seg[0] -= centerX;
                    seg[1] -= centerY;
                    seg[2] -= centerZ;
                }
            }
            float[] v = new float[vertices.size() * 8];
            int i = 0;
            for(float[] seg : vertices) {
                System.arraycopy(seg, 0, v, i, 8);
                i += 8;
            }
            return v;
        } catch(Exception e) {
            Logger.exception("Cannot get vertices from " + path, e);
            return new float[0];
        }
    }

}
