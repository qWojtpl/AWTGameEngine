package pl.AWTGameEngine.engine.deserializers.models;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.engine.Logger;
import pl.AWTGameEngine.engine.helpers.ModelHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * O3DLoader is used to load .o3d files and get vertices from it.
 */
public class O3DLoader {

    public static float[] getVertices(String path, boolean center) {
        Logger.info("Using O3D model loader!");
        ByteBuffer buffer = Dependencies.getResourceManager().getResourceAsByteBuffer(path);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        if((buffer.get() & 0xFF) != 0x84 || (buffer.get() & 0xFF) != 0x19) {
            throw new RuntimeException("File is not valid O3D model!");
        }
        buffer.get();
        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        boolean done = false;
        while(buffer.hasRemaining()) {
            byte section = buffer.get();
            switch(section & 0xFF) {
                case 0x17:
                    int numVertices = buffer.getShort();
                    for(int i = 0; i < numVertices; i++) {
                        for(int j = 0; j < 8; j++) {
                            vertices.add(buffer.getFloat());
                        }
                    }
                    break;
                case 0x49:
                    int numTriangles = buffer.getShort();
                    for(int i = 0; i < numTriangles; i++) {
                        int b1 = buffer.getShort();
                        int b2 = buffer.getShort();
                        int b3 = buffer.getShort();
                        buffer.getShort();
                        indices.add(b3);
                        indices.add(b2);
                        indices.add(b1);
                    }
                    done = true;
                    break;
            }
            if(done) {
                break;
            }
        }
        float[] unpackedVertices = new float[indices.size() * 8];
        int ptr = 0;

        for (int index : indices) {
            int baseOffset = index * 8;

            unpackedVertices[ptr++] = vertices.get(baseOffset);     // X
            unpackedVertices[ptr++] = vertices.get(baseOffset + 1); // Y
            unpackedVertices[ptr++] = vertices.get(baseOffset + 2); // Z
            unpackedVertices[ptr++] = vertices.get(baseOffset + 3); // NX
            unpackedVertices[ptr++] = vertices.get(baseOffset + 4); // NY
            unpackedVertices[ptr++] = vertices.get(baseOffset + 5); // NZ
            unpackedVertices[ptr++] = vertices.get(baseOffset + 6); // U
            unpackedVertices[ptr++] = vertices.get(baseOffset + 7); // V
        }

        List<float[]> v = new ArrayList<>();
        for(int i = 0; i < unpackedVertices.length; i += 8) {
            float[] r = new float[]{
                    unpackedVertices[i],
                    unpackedVertices[i + 1],
                    unpackedVertices[i + 2],
                    unpackedVertices[i + 3],
                    unpackedVertices[i + 4],
                    unpackedVertices[i + 5],
                    unpackedVertices[i + 6],
                    unpackedVertices[i + 7]
            };
            v.add(r);
        }

        if(center) {
            ModelHelper.centerCorrection(v);
        }

        return ModelHelper.convertToArray(v);
    }

}
