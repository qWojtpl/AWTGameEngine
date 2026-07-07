package pl.AWTGameEngine.components;

import pl.AWTGameEngine.Dependencies;
import pl.AWTGameEngine.annotations.components.types.ComponentGL;
import pl.AWTGameEngine.annotations.methods.FromXML;
import pl.AWTGameEngine.annotations.methods.SaveState;
import pl.AWTGameEngine.components.base.ObjectComponent;
import pl.AWTGameEngine.engine.graphics.GraphicsManager3D;
import pl.AWTGameEngine.engine.loops.BaseLoop;
import pl.AWTGameEngine.engine.panels.PanelGL;
import pl.AWTGameEngine.objects.GameObject;
import pl.AWTGameEngine.objects.render.ParticleMeta;
import pl.AWTGameEngine.objects.render.RenderOptions3D;
import pl.AWTGameEngine.objects.render.Sprite;
import pl.AWTGameEngine.objects.transform.QuaternionTransformSet;
import pl.AWTGameEngine.objects.transform.TransformSet;

import java.util.ArrayList;
import java.util.List;

@ComponentGL
public class ParticleEmitter extends ObjectComponent {

    private boolean loop = true;
    private ParticleLoop particleLoop;
    private GraphicsManager3D graphicsManager3D;
    private final List<ParticleMeta> particles = new ArrayList<>();
    private long particleCounter = 0;
    private TransformSet iterationStep = new TransformSet();
    private String vectors;
    private final List<TransformSet> parsedVectors = new ArrayList<>();
    private TransformSet particleSize = new TransformSet(10, 10, 10);
    private long ttl = 1200;
    private double iterationsPerSecond = 10;
    private long fadeOutStart = 600;
    private Sprite sprite;

    public ParticleEmitter(GameObject object) {
        super(object);
    }

    @Override
    public void onSerializationFinish() {
        graphicsManager3D = ((PanelGL) getScene().getPanel()).getGraphicsManager3D();
        particleLoop = new ParticleLoop();
        particleLoop.setTargetFps(iterationsPerSecond);
        particleLoop.setUnlockBlock(true);
        particleLoop.start();
    }

    @Override
    public void onRemoveComponent() {
        particleLoop.kill();
    }

    @Override
    public void onUpdate() {
        if(graphicsManager3D == null || particles.isEmpty()) {
            return;
        }
        List<ParticleMeta> p = new ArrayList<>(particles);
        for(ParticleMeta meta : p) {
            if(meta == null) { //TODO: change arraylist
                continue;
            }
            TransformSet newPosition = meta.iterate(iterationStep);
            if(newPosition == null) {
                particles.remove(meta);
                graphicsManager3D.removeRenderable(meta.getIdentifier());
            } else {
                if(ttl - meta.getTtl() >= fadeOutStart) {
                    meta.getRenderable().setOpacity((meta.getTtl()) / (float) (ttl - fadeOutStart));
                }
            }
        }
    }

    public void createParticle(TransformSet updateVector) {
        String particleIdentifier = getObject().getIdentifier() + "$PARTICLE" + particleCounter++;
        RenderOptions3D renderable = new RenderOptions3D(particleIdentifier);
        renderable
                .setShapePath("models/plane.obj")
                .setShader("shaders/billboard")
                .setSprite(sprite == null ? Dependencies.getResourceManager().getResourceAsSprite("sprites/default.jpg") : sprite)
                .setSize(particleSize.clone())
                .setQuaternionRotation(new QuaternionTransformSet())
                .setPosition(getObject().getPosition().clone());
        ParticleMeta meta = new ParticleMeta(renderable, updateVector.clone(), ttl);
        particles.add(meta);
        graphicsManager3D.createRenderable(renderable);
    }

    @SaveState(name = "looped")
    public boolean isLooped() {
        return this.loop;
    }

    @FromXML
    public void setLooped(boolean loop) {
        this.loop = loop;
    }

    @SaveState(name = "iterationStep")
    public TransformSet getIterationStep() {
        return this.iterationStep;
    }

    @FromXML
    public void setIterationStep(TransformSet iterationStep) {
        this.iterationStep = iterationStep;
    }

    @SaveState(name = "vectors")
    public String getVectors() {
        return this.vectors;
    }

    @FromXML
    public void setVectors(String vectors) {
        parsedVectors.clear();
        String[] split = vectors.trim().split("\\s+");
        double x = 0, y = 0, z;
        for(int i = 0, j = 0; i < split.length; i++, j++) {
            if(j == 0) {
                x = Double.parseDouble(split[i]);
            } else if(j == 1) {
                y = Double.parseDouble(split[i]);
            } else if(j == 2) {
                z = Double.parseDouble(split[i]);
                parsedVectors.add(new TransformSet(x, y, z));
                j = -1;
            }
        }
        this.vectors = vectors;
    }

    @SaveState(name = "particleSize")
    public TransformSet getParticleSize() {
        return this.particleSize;
    }

    @FromXML
    public void setParticleSize(TransformSet particleSize) {
        this.particleSize = particleSize;
    }

    @SaveState(name = "ttl")
    public long getTtl() {
        return this.ttl;
    }

    @FromXML
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    @SaveState(name = "fadeOutStart")
    public long getFadeOutStart() {
        return this.fadeOutStart;
    }

    @FromXML
    public void setFadeOutStart(long start) {
        this.fadeOutStart = start;
    }

    @SaveState(name = "iterationsPerSecond")
    public double getIterationsPerSecond() {
        return this.iterationsPerSecond;
    }

    @FromXML
    public void setIterationsPerSecond(double iterationsPerSecond) {
        this.iterationsPerSecond = iterationsPerSecond;
        if(particleLoop != null) {
            particleLoop.setTargetFps(iterationsPerSecond);
        }
    }

    @SaveState(name = "spriteSource")
    public String getSpriteSource() {
        return this.sprite.getImagePath();
    }

    @FromXML
    public void setSpriteSource(String spriteSource) {
        setSprite(Dependencies.getResourceManager().getResourceAsSprite(spriteSource));
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    class ParticleLoop extends BaseLoop {

        public ParticleLoop() {
            super(null, "ParticleLoop");
        }

        @Override
        protected void iteration() {
            if(loop && graphicsManager3D != null) {
                for(TransformSet vector : parsedVectors) {
                    createParticle(vector);
                }
            }
        }

        @Override
        protected void everySecondIteration() {

        }

    }

}
