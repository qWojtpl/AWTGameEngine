package pl.AWTGameEngine.objects.render;

import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.transform.QuaternionTransformSet;
import pl.AWTGameEngine.objects.transform.Vector3;

public class RenderOptions3D {

    private final String identifier;
    private Vector3 position;
    private Vector3 size;
    private Vector3 rotation;
    private QuaternionTransformSet quaternionRotation;
    private Sprite sprite;
    private ColorObject color;
    private String shader;
    private boolean xrayRender = false;
    private String shapePath;
    private float opacity = 1;
    private int repeatTexture = 1;

    public RenderOptions3D(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getSize() {
        return size;
    }

    public Vector3 getRotation() {
        return rotation;
    }

    public QuaternionTransformSet getQuaternionRotation() {
        return quaternionRotation;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public ColorObject getColor() {
        return color;
    }

    public String getShader() {
        return this.shader;
    }

    public String getShapePath() {
        return this.shapePath;
    }

    public boolean isXrayRender() {
        return xrayRender;
    }

    public float getOpacity() {
        return this.opacity;
    }

    public int getRepeatTexture() {
        return this.repeatTexture;
    }

    public RenderOptions3D setPosition(Vector3 position) {
        this.position = position;
        return this;
    }

    public RenderOptions3D setSize(Vector3 size) {
        this.size = size;
        return this;
    }

    public RenderOptions3D setRotation(Vector3 rotation) {
        this.rotation = rotation;
        return this;
    }

    public RenderOptions3D setQuaternionRotation(QuaternionTransformSet rotation) {
        this.quaternionRotation = rotation;
        return this;
    }

    public RenderOptions3D setSprite(Sprite sprite) {
        this.sprite = sprite;
        return this;
    }

    public RenderOptions3D setColor(ColorObject color) {
        this.color = color;
        return this;
    }

    public RenderOptions3D setShader(String shader) {
        this.shader = shader;
        return this;
    }

    public RenderOptions3D setXrayRender(boolean xrayRender) {
        this.xrayRender = xrayRender;
        return this;
    }

    public RenderOptions3D setShapePath(String path) {
        this.shapePath = path;
        return this;
    }

    public RenderOptions3D setOpacity(float opacity) {
        this.opacity = opacity;
        return this;
    }

    public RenderOptions3D setRepeatTexture(int repeatTexture) {
        this.repeatTexture = repeatTexture;
        return this;
    }

}