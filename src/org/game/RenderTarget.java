package org.game;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

final class RenderTarget extends Resource {
    
    public final Texture texture;

    private final int framebuffer;
    private final int renderbuffer;
    private final IntBuffer viewport = BufferUtils.createIntBuffer(16);

    public RenderTarget(int w, int h, ColorFormat format) throws Exception {
        texture = new Texture(null, w, h, format, null);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.id);
        framebuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
        renderbuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderbuffer);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT32F, w, h);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, renderbuffer);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture.id, 0);

        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        if(status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("failed to allocated render target");
        }
    }

    public void begin() {
        viewport.position(0);
        viewport.limit(viewport.capacity());
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
        GL11.glViewport(0, 0, texture.w, texture.h);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebuffer);
    }

    public void end() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));
        viewport.limit(viewport.capacity());
        viewport.position(0);
    }


    @Override
    void destroy() throws Exception {
        texture.destroy();
        GL30.glDeleteFramebuffers(framebuffer);
        GL30.glDeleteRenderbuffers(renderbuffer);

        super.destroy();
    }
}
