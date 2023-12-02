package pl.AWTGameEngine.components;

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
    public void onRender(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY()),
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY() + getObject().getScaleY())
        );
        g.drawLine(
                getCamera().parseX(getObject(), getObject().getX()),
                getCamera().parseY(getObject(), getObject().getY() + getObject().getScaleY()),
                getCamera().parseX(getObject(), getObject().getX() + getObject().getScaleX()),
                getCamera().parseY(getObject(), getObject().getY() + getObject().getScaleY())
        );
        int i = 0;
        int[] keys = new int[values.size()];
        int sum = 0;
        for(int key : values.keySet()) {
            keys[i++] = key;
            sum += key;
        }
        int stepX = getObject().getScaleX() / sum;
        int stepY = 0;
        int delta = 1;
        for(i = 0; i < values.size(); i++) {
            int x = keys[i];
            if(i > 0) {
                delta = x - keys[i - 1];
                System.out.println(delta);
            }
            int y = values.get(x);
            x += stepX * x * i;
            y += 0;
            g.fillOval(getCamera().parseX(getObject(), getRelativeChartX(x)), getCamera().parseY(getObject(), getRelativeChartY(y)), 10, 10);
        }
    }

    public int getRelativeChartX(int x) {
        return x + getObject().getX();
    }

    public int getRelativeChartY(int y) {
        return getObject().getY() + getObject().getScaleY() - y;
    }

}