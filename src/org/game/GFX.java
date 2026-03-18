package org.game;

import org.lwjgl.opengl.GL11;

public final class GFX {
    
    public static void setDepthState(DepthState state) {
        if(state == DepthState.NONE) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
        } else {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            if(state == DepthState.READONLY) {
                GL11.glDepthMask(false);
            } else {
                GL11.glDepthMask(true);
            }
        }
    }

    public static void setBlendState(BlendState state) {
        if(state == BlendState.OPAQUE) {
            GL11.glDisable(GL11.GL_BLEND);
        } else {
            GL11.glEnable(GL11.GL_BLEND);
            if(state == BlendState.ADDITIVE) {
                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
            } else {
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }
        }
    }

    public static void setCullState(CullState state) {
        if(state == CullState.NONE) {
            GL11.glDisable(GL11.GL_CULL_FACE);
        } else {
            GL11.glEnable(GL11.GL_CULL_FACE);
            if(state == CullState.BACK) {
                GL11.glCullFace(GL11.GL_BACK);
            } else {
                GL11.glCullFace(GL11.GL_FRONT);
            }
        }
    }

    public static void clear(float r, float g, float b, float a) {
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(1, 1);
        setDepthState(DepthState.READWRITE);
        setCullState(CullState.BACK);
        setBlendState(BlendState.OPAQUE);
        GL11.glClearColor(r, g, b, a);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    private static boolean hasError = false;

    public static void checkError(String tag) {
        if(!hasError) {
            int code = GL11.glGetError();

            if(code != GL11.GL_NO_ERROR) {
                hasError = true;
                Log.put(0, tag + ":" + code);
            }
        }
    }

    private static final Mat4 m = new Mat4();
    private static final Vec4 v = new Vec4();

    public static Vec3 unproject(float x, float y, float z, float vx, float vy, float vw, float vh, Mat4 projection, Mat4 view, Vec3 p) {
        m.set(projection).mul(view).invert();

        v.x = 2 * (x - vx) / vw - 1;
        v.y = 2 * (y - vy) / vh - 1;
        v.z = 2 * z - 1;
        v.w = 1;
        m.transform(v);
        v.scale(1 / v.w);

        return p.set(v.x, v.y, v.z);
    }
}
