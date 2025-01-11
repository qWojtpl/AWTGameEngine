package pl.AWTGameEngine.objects;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a colored object with optional alpha transitions.
 */
public class ColorObject {

    private static final ScheduledExecutorService TRANSITION_EXECUTOR =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "ColorTransitionThread");
                t.setDaemon(true); // So the application can exit cleanly
                return t;
            });

    private Color color;
    private TransientTransition currentTransition;

    public ColorObject() {
        this(Color.BLACK);
    }

    public ColorObject(Color color) {
        setColor(color);
    }

    public ColorObject(String colorString) {
        setColor(colorString);
    }

    /**
     * Serialize a ColorObject into "rgb(r,g,b)" or "rgba(r,g,b,a)" format.
     */
    public static String serialize(ColorObject object) {
        Color c = object.getColor();
        if (c.getAlpha() < 255) {
            return String.format(Locale.ROOT, "rgba(%d,%d,%d,%d)",
                    c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        } else {
            return String.format(Locale.ROOT, "rgb(%d,%d,%d)",
                    c.getRed(), c.getGreen(), c.getBlue());
        }
    }

    /**
     * Serialize this ColorObject into its string representation.
     */
    public String serialize() {
        return serialize(this);
    }

    /**
     * Convert an "rgb(...)" or "rgba(...)" string to a Color object, or
     * fallback to reflection-based named colors (e.g., "black", "red").
     */
    public static Color deserialize(String colorString) {
        if (colorString == null || colorString.isEmpty()) {
            return Color.BLACK;
        }

        String lower = colorString.toLowerCase(Locale.ROOT).trim();
        if (lower.startsWith("rgb")) {
            return parseRgbString(lower);
        }

        // Attempt to parse named color via reflection
        try {
            Field field = Color.class.getField(lower);
            return (Color) field.get(null);
        } catch (Exception e) {
            // If no matching field, fallback to black
            return Color.BLACK;
        }
    }

    /**
     * Gradually changes the alpha value from the current alpha to `newAlpha` over `time` milliseconds.
     * Any ongoing transition is cancelled before starting a new one.
     */
    public void transientAlpha(int newAlpha, long timeMillis) {
        cancelTransitionIfAny();
        currentTransition = new TransientTransition(this, newAlpha, timeMillis);
        currentTransition.start();
    }

    /**
     * Get the current Color.
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Check whether the alpha is currently transitioning.
     */
    public boolean isTransiting() {
        return (currentTransition != null && !currentTransition.isCancelled());
    }

    /**
     * Set the current Color directly.
     */
    public void setColor(Color color) {
        this.color = color != null ? color : Color.BLACK;
    }

    /**
     * Set the current Color from a string, using `deserialize`.
     */
    public void setColor(String colorString) {
        this.color = deserialize(colorString);
    }

    /**
     * Cancel any active alpha transition.
     */
    public void cancelTransitionIfAny() {
        if (currentTransition != null) {
            currentTransition.cancel();
            currentTransition = null;
        }
    }

    /**
     * Helper method to parse "rgb(...)" or "rgba(...)" strings.
     */
    private static Color parseRgbString(String rgbString) {
        // e.g. "rgba(255,0,123,128)" or "rgb(255,255,255)"
        String digits = rgbString.replaceAll("[a-z()]", "");
        String[] split = digits.split(",");
        // Expected 3 for rgb or 4 for rgba
        if (split.length < 3 || split.length > 4) {
            return Color.BLACK;
        }
        try {
            int r = Integer.parseInt(split[0].trim());
            int g = Integer.parseInt(split[1].trim());
            int b = Integer.parseInt(split[2].trim());
            int a = (split.length == 4) ? Integer.parseInt(split[3].trim()) : 255;
            return new Color(r, g, b, a);
        } catch (NumberFormatException e) {
            return Color.BLACK;
        }
    }

    /**
     * Inner class to manage alpha transitions over time using a scheduled executor.
     */
    private static class TransientTransition {
        private final ColorObject parent;
        private final int targetAlpha;
        private final long timeMillis;
        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        // We'll calculate how much alpha to change each step.
        private int stepSize;
        private long stepDelay;
        private int currentAlpha;

        public TransientTransition(ColorObject parent, int newAlpha, long timeMillis) {
            this.parent = parent;
            this.targetAlpha = Math.max(0, Math.min(255, newAlpha)); // clamp 0-255
            this.timeMillis = Math.max(1, timeMillis);               // prevent zero or negative
            this.currentAlpha = parent.getColor().getAlpha();
        }

        public void start() {
            int alphaDiff = targetAlpha - currentAlpha;
            int steps = Math.abs(alphaDiff);
            if (steps == 0) {
                return; // no change needed
            }

            // The time is split into 'steps' intervals
            stepDelay = timeMillis / steps;
            stepSize = (alphaDiff < 0) ? -1 : 1;

            // Schedule repeated tasks for each alpha increment
            for (int i = 1; i <= steps; i++) {
                long delay = i * stepDelay;
                TRANSITION_EXECUTOR.schedule(this::updateAlpha, delay, TimeUnit.MILLISECONDS);
            }
        }

        /**
         * Increment or decrement alpha by 1 step, then update the parent's Color.
         */
        private void updateAlpha() {
            if (cancelled.get()) {
                return;
            }

            currentAlpha += stepSize;
            Color oldColor = parent.getColor();
            parent.setColor(new Color(
                    oldColor.getRed(),
                    oldColor.getGreen(),
                    oldColor.getBlue(),
                    currentAlpha
            ));
        }

        public void cancel() {
            cancelled.set(true);
        }

        public boolean isCancelled() {
            return cancelled.get();
        }
    }
}

