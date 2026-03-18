package org.game;


import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

public final class SceneRenderer extends Renderer {
    
    private final Vector<SceneNode> renderables = new Vector<>();
    private final Vector<SceneNode> lights = new Vector<>();
    private final Vector<SceneNode> meshes = new Vector<>();
    private int trianglesRendered = 0;
    private SceneNode axis = new SceneNode();
    private final Mat4 matrix = new Mat4();

    public int getTrianglesRendered() {
        return trianglesRendered;
    }

    void render(Scene scene) throws Exception {

        scene.calcBoundsAndTransform();
        scene.root.traverse((n) -> {
            n.init();
            return true;
        });
        scene.calcBoundsAndTransform();
        scene.root.traverse((n) -> {
            n.start();
            return true;
        });
        scene.calcBoundsAndTransform();
        scene.root.traverse((n) -> {
            n.update(scene);
            return true;
        });
        scene.calcBoundsAndTransform();

        if(scene.brush != null) {
            scene.brush.calcBoundsAndTransform();
        }

        addNodes(scene);

        renderNodes(scene);

        scene.root.traverse((n) -> {
            for(int i = 0; i != n.getComponentCount(); i++) {
                String s = n.getComponent(i).loadSceneName();

                if(s != null) {
                    scene.setLoadFile(IO.file(AssetManager.getRoot(), s + ".scn"));
                }
            }
            return true;
        });

        clearNodes();
    }

    private void addNodes(Scene scene) throws Exception {
        clearNodes();

        scene.root.traverse((n) -> {
            if(n.visible) {
                if(n.emitsLight) {
                    lights.add(n);
                }
                return true;
            }
            return false;
        });

        if(scene.brush != null) {
            scene.brush.traverse((n) -> {
                if(n.visible) {
                    if(n.emitsLight) {
                        lights.add(n);
                    }
                    return true;
                }
                return false;
            });
        }

        lights.sort((a, b) -> {
            float da = a.absolutePosition.distance(scene.target);
            float db = b.absolutePosition.distance(scene.target);

            return Float.compare(da, db);
        });

        trianglesRendered = 0;

        scene.root.traverse((n) -> {
            if(n.visible) {
                if(n.renderable != null || n.getComponentCount() != 0) {
                    renderables.add(n);
                } else if(n.emitsLight || n.isLocation || n.drawLines) {
                    if(scene.isInDesign()) {
                        renderables.add(n);
                    }
                }
                return true;
            }
            return false;
        });

        if(scene.isInDesign()) {
            renderables.add(axis);
        }

        if(scene.brush != null) {
            scene.brush.traverse((n) -> {
                if(n.visible) {
                    if(n.renderable != null || n.getComponentCount() != 0) {
                        renderables.add(n);
                    } else if(n.emitsLight || n.isLocation || n.drawLines) {
                        if(scene.isInDesign()) {
                            renderables.add(n);
                        }
                    }
                    return true;
                }
                return false;
            });
        }

        renderables.sort((a, b) -> {
            if(a.zOrder == b.zOrder) {
                float da = a.absolutePosition.distance(scene.eye);
                float db = b.absolutePosition.distance(scene.eye);

                return Float.compare(db, da);
            } else {
                return Integer.compare(a.zOrder, b.zOrder);
            }
        });
    }

    private void clearNodes() {
        renderables.clear();
        lights.clear();
        meshes.clear();
    }

    private void renderNodes(Scene scene) throws Exception {
        GFX.clear(scene.backgroundColor.x, scene.backgroundColor.y, scene.backgroundColor.z, scene.backgroundColor.w);

        DepthState depthState = null;
        CullState cullState = null;
        BlendState blendState = null;

        LineRenderer lineRenderer = Game.getInstance().getRenderer(LineRenderer.class);

        for(SceneNode node : renderables) {
            if(depthState != node.depthState) {
                GFX.setDepthState(depthState = node.depthState);
            }
            if(cullState != node.cullState) {
                GFX.setCullState(cullState = node.cullState);
            }
            if(blendState != node.blendState) {
                GFX.setBlendState(blendState = node.blendState);
            }
            if(node.renderable != null) {
                trianglesRendered += node.renderable.render(scene, node, lights);
            } else if((node.emitsLight || node.isLocation || node.drawLines) && scene.isInDesign()) {
                lineRenderer.begin(scene.projection, scene.view, matrix.toIdentity());
                if(node.drawTreeLines) {
                    drawTreeLines(node, lineRenderer);
                } else if(node.drawLines) {
                    for(int i = 0; i < node.getChildCount() - 1; i++) {
                        Vec3 p1 = node.getChild(i).absolutePosition;
                        Vec3 p2 = node.getChild(i + 1).absolutePosition;

                        lineRenderer.push(p1.x, p1.y, p1.z, 1, 0.5f, 0, 1, p2.x, p2.y, p2.z, 1, 0.5f, 0, 1);
                    }
                }
                Vec3 p = node.absolutePosition;
                Vec3 r = node.r;
                Vec3 u = node.u;
                Vec3 f = node.f;

                lineRenderer.push(p.x, p.y, p.z, 1, 0, 0, 1, p.x + r.x * 16, p.y + r.y * 16, p.z + r.z * 16, 1, 0, 0, 1);
                lineRenderer.push(p.x, p.y, p.z, 0, 1, 0, 1, p.x + u.x * 16, p.y + u.y * 16, p.z + u.z * 16, 0, 1, 0, 1);
                lineRenderer.push(p.x, p.y, p.z, 0, 0, 1, 1, p.x + f.x * 16, p.y + f.y * 16, p.z + f.z * 16, 0, 0, 1, 1);
                lineRenderer.end();
            } else if(node == axis) {
                float l = scene.calcOffset().length() / 6;
                
                lineRenderer.begin(scene.projection, scene.view, matrix.toIdentity().translate(scene.target));
                lineRenderer.push(0, 0, 0, 1, 0, 0, 1, l, 0, 0, 1, 0, 0, 1);
                lineRenderer.push(0, 0, 0, 0, 1, 0, 1, 0, l, 0, 0, 1, 0, 1);
                lineRenderer.push(0, 0, 0, 0, 0, 1, 1, 0, 0, l, 0, 0, 1, 1);
                lineRenderer.end();
            }
        }

        SpriteRenderer spriteRenderer = Game.getInstance().getRenderer(SpriteRenderer.class);

        spriteRenderer.begin();
        scene.root.traverse((n) -> {
            if(n.visible) {
                n.renderSprites(spriteRenderer);
                return true;
            }
            return false;
        });
        spriteRenderer.end();
    }

    private void drawTreeLines(SceneNode node, LineRenderer renderer) {
        Vec3 p1 = node.absolutePosition;

        for(SceneNode child : node) {
            Vec3 p2 = child.absolutePosition;
        
            renderer.push(p1.x, p1.y, p1.z, 1, 0.5f, 0, 1, p2.x, p2.y, p2.z, 1, 0.5f, 0, 1);
            drawTreeLines(child, renderer);
        }
    }

    public void takeShot(Scene scene) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

        takeShot(IO.file(IO.file(AssetManager.getRoot(), "shots"), now.format(formatter) + ".png"), scene);
    }

    public void takeShot(File file, Scene scene) throws Exception {
        RenderTarget target = null;

        try {
            try {
                addNodes(scene);

                target = new RenderTarget(Game.getInstance().w(), Game.getInstance().h(), ColorFormat.COLOR);
                target.begin();
                renderNodes(scene);
                target.end();
            } finally {
                clearNodes();
            }

            file.getParentFile().mkdirs();

            ByteBuffer buf = ByteBuffer.allocateDirect(target.texture.w * target.texture.h * 4).order(ByteOrder.nativeOrder());

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, target.texture.id);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            int[] pixels = new int[target.texture.w * target.texture.h];
            byte[] rgba = new byte[target.texture.w * target.texture.h * 4];

            buf.get(rgba);

            for(int x = 0; x != target.texture.w; x++) {
                for(int y = 0; y != target.texture.h; y++) {
                    int i = (target.texture.h - y - 1) * target.texture.w + x;
                    int j = y * target.texture.w * 4 + x * 4;
                    int r = rgba[j++];
                    int g = rgba[j++];
                    int b = rgba[j];

                    pixels[i] = 0xFF000000 | ((r << 16) & 0xFF0000) | ((g << 8) & 0xFF00) | (b & 0xFF);
                }
            }

            BufferedImage image = new BufferedImage(target.texture.w, target.texture.h, BufferedImage.TYPE_INT_ARGB);

            image.setRGB(0, 0, target.texture.w, target.texture.h, pixels, 0, target.texture.w);

            ImageIO.write(image, "PNG", file);
        } catch(Exception ex) {
            Log.put(0, ex);
        } finally {
            if(target != null) {
                target.destroy();
            }
        }
    }

    @Override
    void destroy() throws Exception {
        super.destroy();
    }
}

