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

    public static float[] getVertices(String path) {
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
            List<Float> vertices = new ArrayList<>();
            for(int i = 0; i < scene.mNumMeshes(); i++) {
                AIMesh mesh = AIMesh.create(meshes.get(i));
                for(int j = 0; j < mesh.mNumVertices(); j++) {
                    AIVector3D pos = mesh.mVertices().get(j);
                    vertices.add(pos.x());
                    vertices.add(pos.y());
                    vertices.add(pos.z());
                    AIVector3D normal = mesh.mNormals().get(j);
                    vertices.add(normal.x());
                    vertices.add(normal.y());
                    vertices.add(normal.z());
                    if(mesh.mNumUVComponents(0) > 0) {
                        AIVector3D uv = mesh.mTextureCoords(0).get(j);
                        vertices.add(uv.x());
                        vertices.add(1.0f - uv.y());
                    } else {
                        vertices.add(0f);
                        vertices.add(0f);
                    }
                }
                mesh.close();
            }
            float[] v = new float[vertices.size()];
            for(int i = 0; i < v.length; i++) {
                v[i] = vertices.get(i);
            }
            return v;
        } catch(Exception e) {
            Logger.exception("Cannot get vertices from " + path, e);
            return new float[0];
        }
    }

}
