package pl.AWTGameEngine.engine.helpers;

import java.util.List;

public class ModelHelper {

    /**
     * Method is used to move whole model to the center.
     */
    public static void centerCorrection(List<float[]> vertices) {
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

    /**
     * Converts vertices List of float[] to one float array.
     * @param vertices Vertices input
     * @return Parsed float array
     */
    public static float[] convertToArray(List<float[]> vertices) {
        float[] v = new float[vertices.size() * 8];
        int i = 0;
        for(float[] seg : vertices) {
            System.arraycopy(seg, 0, v, i, 8);
            i += 8;
        }
        return v;
    }

}
