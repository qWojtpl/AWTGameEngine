package pl.AWTGameEngine.engine.deserializers;

import pl.AWTGameEngine.Dependencies;

import java.util.ArrayList;
import java.util.List;

public class ObjLoader {

    public static float[] getVertices(String path) {
        List<String> lines = Dependencies.getResourceManager().getResource(path);
        List<float[]> positions = new ArrayList<>();
        List<float[]> uvs = new ArrayList<>();
        List<Float> result = new ArrayList<>();
        for(String line : lines) {
            if(line.startsWith("#") || line.isEmpty()) {
                continue;
            }
            line = line.trim().replaceAll(" +", " ");
            String[] split = line.split(" ");
            if(split[0].equals("v")) {
                positions.add(new float[]{
                        Float.parseFloat(split[1]),
                        Float.parseFloat(split[2]),
                        Float.parseFloat(split[3])
                });
            } else if(split[0].equals("vt")) {
                uvs.add(new float[]{
                        Float.parseFloat(split[1]),
                        Float.parseFloat(split[2])
                });
            } else if(split[0].equals("f")) {
                String[] s = line.split(" ");

                int[] vIdx = new int[s.length - 1];
                int[] vtIdx = new int[s.length - 1];

                for (int i = 1; i < s.length; i++) {
                    String[] parts = s[i].split("/");
                    vIdx[i - 1] = Integer.parseInt(parts[0]) - 1;
                    vtIdx[i - 1] = Integer.parseInt(parts[1]) - 1;
                }

                for (int i = 1; i < vIdx.length - 1; i++) {
                    addResult(result, positions.get(vIdx[0]), uvs.get(vtIdx[0]));
                    addResult(result, positions.get(vIdx[i]), uvs.get(vtIdx[i]));
                    addResult(result, positions.get(vIdx[i + 1]), uvs.get(vtIdx[i + 1]));
                }
            }
        }
        float[] resultArray = new float[result.size()];
        for(int i = 0; i < result.size(); i++) {
            resultArray[i] = result.get(i);
        }
        return resultArray;
    }

    private static void addResult(List<Float> results, float[] position, float[] uv) {
        results.add(position[0]);
        results.add(position[1]);
        results.add(position[2]);
        results.add(uv[0]);
        results.add(1.0f - uv[1]);
    }

}
