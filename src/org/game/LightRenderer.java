package org.game;

import java.nio.FloatBuffer;
import java.util.Vector;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

final class LightRenderer extends Renderer {
    
    public static final int VERTEX_STRIDE = 12;
    public static final int MAX_LIGHTS = 6;
    
    private final Shader shader;
    private final int vbo;
    private FloatBuffer vBuf = BufferUtils.createFloatBuffer(6 * VERTEX_STRIDE);

    public LightRenderer() throws Exception {
        shader = new Shader(
            IO.readAllBytes(Shader.class, "/org/game/glsl/LightVertexShader.glsl"), 
            IO.readAllBytes(Shader.class, "/org/game/glsl/LightFragmentShader.glsl"),
            "aPosition", "aTextureCoordinate", "aNormal", "aColor"
        );

        vbo = GL15.glGenBuffers();
    }

    void begin(Scene scene, SceneNode node, Vector<SceneNode> lights, Texture texture, Texture decal) throws Exception {
        int count = Math.min(MAX_LIGHTS, lights.size());

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        shader.begin();
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, VERTEX_STRIDE * 4, 0);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, VERTEX_STRIDE * 4, 4 * 3);
        GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, VERTEX_STRIDE * 4, 4 * 5);
        GL20.glVertexAttribPointer(3, 4, GL11.GL_FLOAT, false, VERTEX_STRIDE * 4, 4 * 8);
        shader.set("uProjection", scene.projection);
        shader.set("uView", scene.view);
        shader.set("uModel", node.model);
        shader.set("uModelIT", node.modelIT);
        shader.set("uEye", scene.eye);
        shader.set("uTextureEnabled", texture != null);
        if(texture != null) {
            shader.bind(GL11.GL_TEXTURE_2D, "uTexture", 0, texture.id);
        }
        shader.set("uDecalTextureEnabled", decal != null);
        if(decal != null) {
            shader.bind(GL11.GL_TEXTURE_2D, "uDecalTexture", 1, decal.id);
        }
        shader.set("uAmbientColor", node.ambientColor);
        shader.set("uDiffuseColor", node.diffuseColor);
        shader.set("uSpecularColor", node.specularColor);
        shader.set("uSpecularPower", node.specularPower);
        shader.set("uLightCount", count);
        shader.set("uLightingEnabled", node.receivesLight);
        for(int i = 0; i != count; i++) {
            SceneNode l = lights.get(i);

            shader.set("uLightPosition[" + i + "]", l.absolutePosition);
            shader.set("uLightColor[" + i + "]", l.lightColor);
            shader.set("uLightRadius[" + i + "]", l.lightRadius);
        }
        shader.set("uWarpEnabled", node.warpEnabled);
        shader.set("uWarpAmplitude", node.warpAmplitude);
        shader.set("uWarpFrequency", node.warpFrequency);
        shader.set("uWarpTime", Game.getInstance().totalTime() * node.warpSpeed);
        shader.set("uWarpY", node.warpY);
        vBuf.limit(vBuf.capacity());
        vBuf.position(0);
    }

    void push(Vector<Vertex> vertices, Vector<Integer> indices, int indexCount) {
        if(vBuf.position() + indices.size() * VERTEX_STRIDE > vBuf.capacity()) {
            int newCapacity = vBuf.position() + indices.size() * VERTEX_STRIDE;

            Log.put(1, "increasing light renderer vertex buffer capacity to " + newCapacity);

            FloatBuffer nBuf = BufferUtils.createFloatBuffer(newCapacity);

            vBuf.flip();
            nBuf.put(vBuf);
            vBuf = nBuf;
        }
        for(int i = 0; i != indexCount; i++) {
            Vertex v = vertices.get(indices.get(i));

            vBuf.put(v.position.x);
            vBuf.put(v.position.y);
            vBuf.put(v.position.z);
            vBuf.put(v.textureCoordinate.x);
            vBuf.put(v.textureCoordinate.y);
            vBuf.put(v.normal.x);
            vBuf.put(v.normal.y);
            vBuf.put(v.normal.z);
            vBuf.put(v.color.x);
            vBuf.put(v.color.y);
            vBuf.put(v.color.z);
            vBuf.put(v.color.w);
        }
    }

    void push(Vertex v) {
        if(vBuf.position() + VERTEX_STRIDE > vBuf.capacity()) {
            int newCapacity = vBuf.position() + 600 * VERTEX_STRIDE;

            Log.put(1, "increasing light renderer vertex buffer capacity to " + newCapacity);

            FloatBuffer nBuf = BufferUtils.createFloatBuffer(newCapacity);

            vBuf.flip();
            nBuf.put(vBuf);
            vBuf = nBuf;
        }

        vBuf.put(v.position.x);
        vBuf.put(v.position.y);
        vBuf.put(v.position.z);
        vBuf.put(v.textureCoordinate.x);
        vBuf.put(v.textureCoordinate.y);
        vBuf.put(v.normal.x);
        vBuf.put(v.normal.y);
        vBuf.put(v.normal.z);
        vBuf.put(v.color.x);
        vBuf.put(v.color.y);
        vBuf.put(v.color.z);
        vBuf.put(v.color.w);
    }

    void end() {
        int count = vBuf.position() / VERTEX_STRIDE;

        if(count > 0) {
            vBuf.flip();
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vBuf, GL15.GL_DYNAMIC_DRAW);
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, count);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        shader.end();
    }

    @Override
    void destroy() throws Exception {
        shader.destroy();
        GL15.glDeleteBuffers(vbo);
        super.destroy();
    }
}
