package pl.AWTGameEngine.objects;

import java.awt.*;
import java.lang.reflect.Field;

public class ColorObject {

    private Color color;
    private Transient transientThread;

    public ColorObject() {
        setColor(Color.BLACK);
    }

    public ColorObject(Color color) {
        setColor(color);
    }

    public ColorObject(String color) {
        setColor(color);
    }

    public static String serialize(ColorObject object) {
        if(object.getColor().getAlpha() != 255) {
            return "rgba(" +
                    object.getColor().getRed() + "," +
                    object.getColor().getGreen() + "," +
                    object.getColor().getBlue() + "," +
                    object.getColor().getAlpha() +
                    ")";
        }
        return "rgb(" +
                object.getColor().getRed() + "," +
                object.getColor().getGreen() + "," +
                object.getColor().getBlue() +
                ")";
    }

    public String serialize() {
        return serialize(this);
    }

    public static Color deserialize(String color) {
        Color c = Color.BLACK;
        if(color.toLowerCase().startsWith("rgb")) {
            color = color.replace("(", "").replace(")", "");
            int argumentCount = 3;
            if(color.toLowerCase().startsWith("rgba")) {
                argumentCount++;
            }
            if(argumentCount == 3) {
                color = color.replace("rgb", "");
            } else {
                color = color.replace("rgba", "");
            }
            String[] split = color.split(",");
            if(split.length != argumentCount) {
                return c;
            }
            try {
                int[] args = new int[]{0, 0, 0, 255};
                for(int i = 0; i < argumentCount; i++) {
                    args[i] = Integer.parseInt(split[i]);
                }
                c = new Color(args[0], args[1], args[2], args[3]);
            } catch(NumberFormatException ignored) {}
            return c;
        }
        try {
            Field field = Class.forName("java.awt.Color").getField(color.toLowerCase());
            c = (Color) field.get(null);
        } catch(Exception ignored) {}
        return c;
    }

    public void transientAlpha(int newAlpha, int time) {
        if(transientThread != null) {
            transientThread.cancel();
        }
        transientThread = new Transient(this, getColor().getAlpha(), newAlpha, time);
        transientThread.start();
    }

    public Color getColor() {
        return this.color;
    }

    public javafx.scene.paint.Color getFxColor() {
        return new javafx.scene.paint.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public boolean isTransiting() {
        return transientThread != null;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(String color) {
        this.color = deserialize(color);
    }

    static class Transient extends Thread {

        private final ColorObject parent;
        private int alpha;
        private final int newAlpha;
        private final double interval;
        private boolean canceled = false;

        public Transient(ColorObject parent, int alpha, int newAlpha, double time) {
            this.parent = parent;
            this.alpha = alpha;
            this.newAlpha = newAlpha;
            double interval = time / Math.abs(newAlpha - alpha);
            if(interval < 1) {
                interval = 1;
            }
            this.interval = interval;
        }

        @Override
        public void run() {
            while(alpha != newAlpha && !canceled) {
                try {
                    Thread.sleep((long) interval);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
                if(alpha > newAlpha) {
                    alpha--;
                } else {
                    alpha++;
                }
                Color c = parent.getColor();
                parent.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
            }
        }

        public void cancel() {
            canceled = true;
        }

    }

}
