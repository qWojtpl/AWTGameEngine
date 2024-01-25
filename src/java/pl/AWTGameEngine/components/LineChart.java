package pl.AWTGameEngine.components;

import pl.AWTGameEngine.engine.GraphicsManager;
import pl.AWTGameEngine.objects.GameObject;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class LineChart extends ObjectComponent {

    private final HashMap<Integer, String> labels = new HashMap<>();
    private final LinkedHashMap<Integer, Integer> values = new LinkedHashMap<>();

    public LineChart(GameObject object) {
        super(object);
    }

    @Override
    public void onAddComponent() {
        values.put(0, 100);
        values.put(10, 50);
        values.put(20, 75);
        values.put(30, 75);
        values.put(40, 70);
        values.put(50, 30);
    }

    @Override
    public void onRender(GraphicsManager g) {
        g.drawLine(
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY()),
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY() + getObject().getSizeY()),
                new GraphicsManager.RenderOptions()
                        .setColor(Color.BLACK)
        );
        g.drawLine(
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY() + getObject().getSizeY()),
                getCamera().parseX(getObject(), getObject().getX() + getObject().getSizeX()),
                getCamera().parseY(getObject(), getObject().getY() + getObject().getSizeY()),
                new GraphicsManager.RenderOptions()
                        .setColor(Color.BLACK)
        );
        int i = 0;
        int[] keys = new int[values.size()];
        int sum = 0;
        for(int key : values.keySet()) {
            keys[i++] = key;
            sum += key;
        }
        int stepX = getObject().getSizeX() / sum;
        for(i = 0; i < values.size(); i++) {
            int x = keys[i];
            int y = values.get(x);
            x += stepX * x * i;
            y += 0;
            //g.fillOval(getCamera().parseX(getObject(), getRelativeChartX(x)), getCamera().parseY(getObject(), getRelativeChartY(y)), 10, 10);
        }
    }

    public int getRelativeChartX(int x) {
        return x + getObject().getX();
    }

    public int getRelativeChartY(int y) {
        return getObject().getY() + getObject().getSizeY() - y;
    }

}
