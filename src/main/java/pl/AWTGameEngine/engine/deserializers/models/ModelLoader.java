package pl.AWTGameEngine.engine.deserializers.models;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.helpers.ModelHelper;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
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

                AIVector3D.Buffer positions = mesh.mVertices();
                AIVector3D.Buffer normals = mesh.mNormals();
                AIVector3D.Buffer uvs = mesh.mTextureCoords(0);

                boolean hasUV = uvs != null && mesh.mNumUVComponents(0) > 0;

                for(int j = 0; j < mesh.mNumFaces(); j++) {
                    AIFace face = mesh.mFaces().get(j);
                    IntBuffer indices = face.mIndices();

                    for(int k = 0; k < face.mNumIndices(); k++) {
                        int index = indices.get(k);

                        float[] seg = new float[8];

                        AIVector3D pos = positions.get(index);
                        AIVector3D normal = normals.get(index);

                        seg[0] = pos.x();
                        seg[1] = pos.y();
                        seg[2] = pos.z();

                        seg[3] = normal.x();
                        seg[4] = normal.y();
                        seg[5] = normal.z();

                        if(hasUV) {
                            AIVector3D uv = uvs.get(index);
                            seg[6] = uv.x();
                            seg[7] = 1.0f - uv.y();
                        }

                        vertices.add(seg);
                    }
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
