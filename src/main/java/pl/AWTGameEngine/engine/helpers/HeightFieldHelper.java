package pl.AWTGameEngine.engine.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class HeightFieldHelper {

    public static List<float[]> generateHeightFieldVertices(BiFunction<Integer, Integer, Short> heightFunction, int rows, int cols, float rowScale, float columnScale, float heightScale) {
        List<float[]> vertices = new ArrayList<>((rows - 1) * (cols - 1) * 6);
        for(int row = 0; row < rows - 1; row++) {
            for(int col = 0; col < cols - 1; col++) {
                vertices.add(vertexOf(heightFunction, row, col, rows, cols, rowScale, columnScale, heightScale));
                vertices.add(vertexOf(heightFunction, row, col + 1, rows, cols, rowScale, columnScale, heightScale));
                vertices.add(vertexOf(heightFunction, row + 1, col, rows, cols, rowScale, columnScale, heightScale));
                vertices.add(vertexOf(heightFunction, row, col + 1, rows, cols, rowScale, columnScale, heightScale));
                vertices.add(vertexOf(heightFunction, row + 1, col + 1, rows, cols, rowScale, columnScale, heightScale));
                vertices.add(vertexOf(heightFunction, row + 1, col, rows, cols, rowScale, columnScale, heightScale));
            }
        }
        return vertices;
    }

    private static float[] vertexOf(BiFunction<Integer, Integer, Short> heightFunction, int row, int col, int rows, int cols, float rowScale, float columnScale, float heightScale) {
        float x = col * columnScale;
        float y = heightFunction.apply(row, col) * heightScale;
        float z = row * rowScale;

        float[] normal = normalOf(heightFunction, row, col, rows, cols, rowScale, columnScale, heightScale);
        float u = col / (float) (cols - 1);
        float v = row / (float) (rows - 1);

        return new float[]{ x, y, z, normal[0], normal[1], normal[2], u, v };
    }

    private static float[] normalOf(BiFunction<Integer, Integer, Short> heightFunction, int row, int col, int rows, int cols, float rowScale, float columnScale, float heightScale) {
        int left = Math.max(col - 1, 0);
        int right = Math.min(col + 1, cols - 1);
        int top = Math.max(row - 1, 0);

        int bottom = Math.min(row + 1, rows - 1);

        float heightLeft = heightFunction.apply(row, left) * heightScale;
        float heightRight = heightFunction.apply(row, right) * heightScale;

        float heightTop = heightFunction.apply(top, col) * heightScale;
        float heightBottom = heightFunction.apply(bottom, col) * heightScale;

        float nx = heightLeft - heightRight;
        float ny = 2.0f * Math.max(rowScale, columnScale);

        float nz = heightTop - heightBottom;

        float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);

        if(length > 0.000001f) {
            nx /= length;
            ny /= length;
            nz /= length;
        }

        return new float[]{ nx, ny, nz };
    }

}
