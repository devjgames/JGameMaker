package org.game;

import java.io.File;
import java.util.Vector;

public final class ParticleSystem implements Renderable {

    public Texture texture = null;
    public final Vec3 emitPosition = new Vec3();

    private float[] particles;
    private float[] temp;
    private int count;
    private final Mat4 m = new Mat4();
    private final Vec3 r = new Vec3();
    private final Vec3 u = new Vec3();
    private final Vec3 f = new Vec3();
    private float time = 0;
    private int stackTop = 0;
    private final int maxParticles;
    private final Vector<Vertex> vertices = new Vector<>();
    private final Vector<Integer> indices = new Vector<>();
    private final AABB bounds = new AABB();
    private int particleCount = 0;
    private File file;

    public ParticleSystem(int maxParticles) throws Exception {
        for(int i = 0; i != maxParticles * 4; i++) {
            vertices.add(new Vertex());
        }
        for(int i = 0; i != maxParticles * 4; i += 4) {
            indices.add(i);
            indices.add(i + 1);
            indices.add(i + 2);
            indices.add(i + 2);
            indices.add(i + 3);
            indices.add(i);
        }
        this.maxParticles = maxParticles;

        count = 0;
        particles = new float[maxParticles * 20];
        temp = new float[maxParticles * 20];
    }

    ParticleSystem(ParticleSystem particles) throws Exception {
        if(texture != null) {
            try {
                texture = Game.getInstance().getAssets().load(texture.file);
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        }

        maxParticles = particles.maxParticles;
        for(int i = 0; i != maxParticles * 4; i++) {
            vertices.add(new Vertex());
        }
        this.indices.addAll(particles.indices);

        count = 0;
        this.particles = new float[maxParticles * 20];
        temp = new float[maxParticles * 20];

        Utils.copy(particles, this);

        file = particles.file;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public int getTriangleCount() {
        return particleCount * 2;
    }

    @Override
    public Triangle getTriangle(Scene scene, SceneNode node, int i, Triangle triangle) {
        i *= 3;
        triangle.p1.set(vertices.get(indices.get(i + 0)).position);
        triangle.p2.set(vertices.get(indices.get(i + 1)).position);
        triangle.p3.set(vertices.get(indices.get(i + 2)).position);
        return triangle.calcPlane();
    }

    public AABB getBounds() {
        return bounds;
    }

    @Override
    public void update(Scene scene, SceneNode node) throws Exception {
        time = Game.getInstance().totalTime();
        int count = 0;
        for(int i = 0; i != this.count; i += 20) {
            float n = (time - particles[i + 18]) / particles[i + 19];
            if(n <= 1) {
                for(int j = 0; j != 20; j++) {
                    temp[count++] = particles[i + j];
                }
            }
        }
        float[] t = particles;
        particles = temp;
        temp = t;
        this.count = count;

        m.set(scene.view).mul(node.model).transpose();
        r.set(m.m00, m.m10, m.m20).normalize();
        u.set(m.m01, m.m11, m.m21).normalize();
        f.set(m.m02, m.m12, m.m22).normalize();

        bounds.clear();
        stackTop = 0;
        particleCount = 0;
        for(int i = 0; i != count; particleCount++) {
            float vX = particles[i++];
            float vY = particles[i++];
            float vZ = particles[i++];
            float pX = particles[i++];
            float pY = particles[i++];
            float pZ = particles[i++];
            float sR = particles[i++];
            float sG = particles[i++];
            float sB = particles[i++];
            float sA = particles[i++];
            float eR = particles[i++];
            float eG = particles[i++];
            float eB = particles[i++];
            float eA = particles[i++];
            float sX = particles[i++];
            float sY = particles[i++];
            float eX = particles[i++];
            float eY = particles[i++];
            float s = time - particles[i++];
            float n = s / particles[i++];
            float cR = sR + n * (eR - sR);
            float cG = sG + n * (eG - sG);
            float cB = sB + n * (eB - sB);
            float cA = sA + n * (eA - sA);
            float x = (sX + n * (eX - sX)) * 0.5f;
            float y = (sY + n * (eY - sY)) * 0.4f;

            pX += s * vX;
            pY += s * vY;
            pZ += s * vZ;
            push(pX - r.x * x - u.x * y, pY - r.y * x - u.y * y, pZ - r.z * x - u.z * y, 0, 0, f.x, f.y, f.z, cR, cG, cB, cA);
            push(pX + r.x * x - u.x * y, pY + r.y * x - u.y * y, pZ + r.z * x - u.z * y, 1, 0, f.x, f.y, f.z, cR, cG, cB, cA);
            push(pX + r.x * x + u.x * y, pY + r.y * x + u.y * y, pZ + r.z * x + u.z * y, 1, 1, f.x, f.y, f.z, cR, cG, cB, cA);
            push(pX - r.x * x + u.x * y, pY - r.y * x + u.y * y, pZ - r.z * x + u.z * y, 0, 1, f.x, f.y, f.z, cR, cG, cB, cA);
            bounds.add(pX - r.x * x - u.x * y, pY - r.y * x - u.y * y, pZ - r.z * x - u.z * y);
            bounds.add(pX + r.x * x - u.x * y, pY + r.y * x - u.y * y, pZ + r.z * x - u.z * y);
            bounds.add(pX + r.x * x + u.x * y, pY + r.y * x + u.y * y, pZ + r.z * x + u.z * y);
            bounds.add(pX - r.x * x + u.x * y, pY - r.y * x + u.y * y, pZ - r.z * x + u.z * y);
        }
    }

    private void push(float x, float y, float z, float s, float t, float nx, float ny, float nz, float r, float g, float b, float a) {
        Vertex v = vertices.get(stackTop++);

        v.position.set(x, y, z);
        v.textureCoordinate.set(s, t);
        v.normal.set(nx, ny, nz);
        v.color.set(r, g, b, a);
    }
    
    public void emit(Particle particle) {
        if(count != particles.length) {
            particles[count++] = particle.velocityX;
            particles[count++] = particle.velocityY;
            particles[count++] = particle.velocityZ;
            particles[count++] = particle.positionX + emitPosition.x;
            particles[count++] = particle.positionY + emitPosition.y;
            particles[count++] = particle.positionZ + emitPosition.z;
            particles[count++] = particle.startR;
            particles[count++] = particle.startG;
            particles[count++] = particle.startB;
            particles[count++] = particle.startA;
            particles[count++] = particle.endR;
            particles[count++] = particle.endG;
            particles[count++] = particle.endB;
            particles[count++] = particle.endA;
            particles[count++] = particle.startX;
            particles[count++] = particle.startY;
            particles[count++] = particle.endX;
            particles[count++]  = particle.endY;
            particles[count++] = Game.getInstance().totalTime();
            particles[count++] = particle.lifeSpan;
        }
    }

    @Override
    public int render(Scene scene, SceneNode node, Vector<SceneNode> lights) throws Exception {
        LightRenderer renderer = Game.getInstance().getRenderer(LightRenderer.class);

        renderer.begin(scene, node, lights, texture, null);
        renderer.push(vertices, indices, particleCount * 6);
        renderer.end();

        return particleCount * 2;
    }

    @Override
    public Renderable newInstance() throws Exception {
        return new ParticleSystem(this);
    }
}
