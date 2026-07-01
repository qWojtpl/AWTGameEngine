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

    public TransformSet iterate(TransformSet iterationStep) {
        if(ttl <= 0) {
            return null;
        }
        ttl--;
        vector.setX(vector.getX() + iterationStep.getX());
        vector.setY(vector.getY() + iterationStep.getY());
        vector.setZ(vector.getZ() + iterationStep.getZ());
        TransformSet currentPosition = renderable.getPosition();
        return currentPosition
                .setX(currentPosition.getX() + vector.getX())
                .setY(currentPosition.getY() + vector.getY())
                .setZ(currentPosition.getZ() + vector.getZ());
    }

    public String getIdentifier() {
        return renderable.getIdentifier();
    }

}
