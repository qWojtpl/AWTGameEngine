package pl.AWTGameEngine.objects.render;

import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.objects.ColorObject;
import pl.AWTGameEngine.objects.transform.QuaternionTransformSet;
import pl.AWTGameEngine.objects.transform.TransformSet;

public class RenderOptions3D {

    private final String identifier;
    private TransformSet position;
    private TransformSet size;
    private TransformSet rotation;
    private QuaternionTransformSet quaternionRotation;
    private Sprite sprite;
    private ColorObject color;
    private GraphicsManager3D.ShapeType shapeType;
    private boolean frontCullFace = false;
    private String shader;
    private boolean xrayRender = false;
    private String shapePath;

    public RenderOptions3D(String identifier) {
        this.identifier = identifier;
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

    public QuaternionTransformSet getQuaternionRotation() {
        return quaternionRotation;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public ColorObject getColor() {
        return color;
    }

    public GraphicsManager3D.ShapeType getShapeType() {
        return this.shapeType;
    }

    public String getShader() {
        return this.shader;
    }

    public String getShapePath() {
        return this.shapePath;
    }

    public boolean isFrontCullFace() {
        return frontCullFace;
    }

    public boolean isXrayRender() {
        return xrayRender;
    }

    public RenderOptions3D setPosition(TransformSet position) {
        this.position = position;
        return this;
    }

    public RenderOptions3D setSize(TransformSet size) {
        this.size = size;
        return this;
    }

    public RenderOptions3D setRotation(TransformSet rotation) {
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

    public RenderOptions3D setFrontCullFace(boolean frontCullFace) {
        this.frontCullFace = frontCullFace;
        return this;
    }

    public RenderOptions3D setXrayRender(boolean xrayRender) {
        this.xrayRender = xrayRender;
        return this;
    }

    public RenderOptions3D setShapeType(GraphicsManager3D.ShapeType shapeType) {
        this.shapeType = shapeType;
        return this;
    }

    public RenderOptions3D setShapePath(String path) {
        this.shapePath = path;
        return this;
    }

}