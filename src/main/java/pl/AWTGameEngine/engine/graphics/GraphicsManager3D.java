
package pl.AWTGameEngine.engine.graphics;

import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.Sprite;
import pl.AWTGameEngine.objects.TransformSet;

public abstract class GraphicsManager3D {

    public abstract void createBox(RenderOptions options);

    public abstract void removeBox(String identifier);

    public abstract void createSphere(RenderOptions options);

    public abstract void removeSphere(String identifier);

    public abstract void createCylinder(RenderOptions options);

    public abstract void removeCylinder(String identifier);

    public abstract void createCustomModel(RenderOptions options, String modelPath);

    public abstract void removeCustomModel(String identifier);

    public abstract void updatePosition(String identifier, ShapeType shape, TransformSet position);

    public abstract void updateSize(String identifier, ShapeType shape, TransformSet scale);

    public abstract void updateRotation(String identifier, ShapeType shape, TransformSet rotation);

    public abstract void updateSprite(String identifier, ShapeType shape, Sprite sprite);

    public abstract void updateColor(String identifier, ShapeType shape, ColorObject color);

    public enum ShapeType {
        BOX,
        CYLINDER,
        SPHERE,
        MODEL
    }

    public static class RenderOptions {

        private final String identifier;
        private TransformSet position;
        private TransformSet size;
        private TransformSet rotation;
        private Sprite sprite;
        private ColorObject color;
        private boolean frontCullFace = false;

        public RenderOptions(String identifier) {
            this.identifier = identifier;
        }

        public RenderOptions(String identifier, TransformSet position, TransformSet size, TransformSet rotation, Sprite sprite, ColorObject color) {
            this.identifier = identifier;
            this.position = position;
            this.size = size;
            this.rotation = rotation;
            this.sprite = sprite;
            this.color = color;
        }

        public RenderOptions(String identifier, TransformSet position, TransformSet size, TransformSet rotation, Sprite sprite, ColorObject color, boolean frontCullFace) {
            this.identifier = identifier;
            this.position = position;
            this.size = size;
            this.rotation = rotation;
            this.sprite = sprite;
            this.color = color;
            this.frontCullFace = frontCullFace;
        }

        public String getIdentifier() {
            return identifier;
        }

        public TransformSet getPosition() {
            return position;
        }

        public TransformSet getSize() {
            return size;
        }

        public TransformSet getRotation() {
            return rotation;
        }

        public Sprite getSprite() {
            return sprite;
        }

        public ColorObject getColor() {
            return color;
        }

        public boolean isFrontCullFace() {
            return frontCullFace;
        }

        public void setPosition(TransformSet position) {
            this.position = position;
        }

        public void setSize(TransformSet size) {
            this.size = size;
        }

        public void setRotation(TransformSet rotation) {
            this.rotation = rotation;
        }

        public void setSprite(Sprite sprite) {
            this.sprite = sprite;
        }

        public void setColor(ColorObject color) {
            this.color = color;
        }

        public void setFrontCullFace(boolean frontCullFace) {
            this.frontCullFace = frontCullFace;
        }

    }

}