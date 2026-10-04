package org.game;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

final class LineRenderer extends Renderer {

    public static final int VERTEX_STRIDE = 7;
    
    private final Shader shader;
    private final int vbo;
    private FloatBuffer vBuf = BufferUtils.createFloatBuffer(6 * VERTEX_STRIDE);

    public LineRenderer() throws Exception {
        shader = new Shader(
            IO.readAllBytes(Shader.class, "/org/game/glsl/ColorVertexShader.glsl"), 
            IO.readAllBytes(Shader.class, "/org/game/glsl/ColorFragmentShader.glsl"),
            "aPosition", "aColor"
        );

        vbo = GL15.glGenBuffers();
    }

    public void begin(Mat4 projection, Mat4 view, Mat4 model) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        shader.begin();
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, VERTEX_STRIDE * 4, 0);
        GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, VERTEX_STRIDE * 4, 4 * 3);
        shader.set("uProjection", projection);
        shader.set("uView", view);
        shader.set("uModel", model);
        vBuf.limit(vBuf.capacity());
        vBuf.position(0);
    }

    public void push(float x1, float y1, float z1, float r1, float g1, float b1, float a1, float x2, float y2, float z2, float r2, float g2, float b2, float a2) {
        if(vBuf.position() + 2 * VERTEX_STRIDE > vBuf.capacity()) {
            int newCapacity = vBuf.position() + 300 * VERTEX_STRIDE;

            Log.put(2, "increasing line renderer vertex buffer capacity to " + newCapacity);

            FloatBuffer nBuf = BufferUtils.createFloatBuffer(newCapacity);

            vBuf.flip();
            nBuf.put(vBuf);
            vBuf = nBuf;
        }
        vBuf.put(x1);
        vBuf.put(y1);
        vBuf.put(z1);
        vBuf.put(r1);
        vBuf.put(g1);
        vBuf.put(b1);
        vBuf.put(a1);
        vBuf.put(x2);
        vBuf.put(y2);
        vBuf.put(z2);
        vBuf.put(r2);
        vBuf.put(g2);
        vBuf.put(b2);
        vBuf.put(a2);
    }

    public void end() {
        int count = vBuf.position() / VERTEX_STRIDE;

        if(count > 0) {
            vBuf.flip();
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vBuf, GL15.GL_DYNAMIC_DRAW);
            GL11.glDrawArrays(GL11.GL_LINES, 0, count);
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
