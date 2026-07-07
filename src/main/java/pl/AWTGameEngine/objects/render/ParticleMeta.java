package pl.AWTGameEngine.objects.render;

import pl.AWTGameEngine.objects.transform.TransformSet;

public class ParticleMeta {

    private final RenderOptions3D renderable;
    private final TransformSet vector;
    private long ttl;

    public ParticleMeta(RenderOptions3D renderable, TransformSet vector, long ttl) {
        this.renderable = renderable;
        this.vector = vector;
        this.ttl = ttl;
    }

    public TransformSet iterate(TransformSet iterationStep, float[] rotation) {
        if(ttl <= 0) {
            return null;
        }
        ttl--;

        double stepX = rotation[0] * iterationStep.getX() + rotation[4] * iterationStep.getY() + rotation[8]  * iterationStep.getZ();
        double stepY = rotation[1] * iterationStep.getX() + rotation[5] * iterationStep.getY() + rotation[9]  * iterationStep.getZ();
        double stepZ = rotation[2] * iterationStep.getX() + rotation[6] * iterationStep.getY() + rotation[10] * iterationStep.getZ();

        vector.setX(vector.getX() + stepX);
        vector.setY(vector.getY() + stepY);
        vector.setZ(vector.getZ() + stepZ);

        double moveX = rotation[0] * vector.getX() + rotation[4] * vector.getY() + rotation[8]  * vector.getZ();
        double moveY = rotation[1] * vector.getX() + rotation[5] * vector.getY() + rotation[9]  * vector.getZ();
        double moveZ = rotation[2] * vector.getX() + rotation[6] * vector.getY() + rotation[10]  * vector.getZ();

        TransformSet currentPosition = renderable.getPosition();
        return currentPosition.set(currentPosition.getX() + moveX, currentPosition.getY() + moveY, currentPosition.getZ() + moveZ);
    }

    public String getIdentifier() {
        return renderable.getIdentifier();
    }

    public long getTtl() {
        return this.ttl;
    }

    public RenderOptions3D getRenderable() {
        return this.renderable;
    }

}
