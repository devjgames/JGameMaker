package org.game;

import java.io.File;

public final class Scene {
    
    public final Vec4 backgroundColor = new Vec4(0.15f, 0.15f, 0.15f, 1);
    public final Vec3 eye = new Vec3(100, 100, 100);
    public final Vec3 target = new Vec3(0, 0, 0);
    public final Vec3 up = new Vec3(0, 1, 0);
    public float fovDegrees = 60;
    public float zNear = 1;
    public float zFar = 25000;
    public final Mat4 projection = new Mat4();
    public final Mat4 view = new Mat4();
    public final SceneNode root = new SceneNode();
    public int snap = 1;
    public String canvasNode = "canvas";

    final File file;
    SceneNode brush = null;

    private final Mat4 m = new Mat4();
    private final Vec3 f = new Vec3();
    private final Vec3 u = new Vec3();
    private final Vec3 r = new Vec3();
    private final Vec3 d = new Vec3();
    private final Vec3 o = new Vec3();
    private final Vec3 origin = new Vec3();
    private final Vec3 direction = new Vec3();
    private final AABB bounds = new AABB();
    private final boolean inDesign;
    private File loadFile = null;

    public Scene(File file, boolean inDesign) {
        this.file = file;
        this.inDesign = inDesign;
    }

    public boolean isInDesign() {
        return inDesign;
    }

    public Vec3 calcOffset() {
        return eye.sub(target, o);
    }

    public String getSceneName() {
        return IO.getFilenameWithoutExtension(file);
    }

    File getLoadFile() {
        return loadFile;
    }

    void setLoadFile(File file) {
        if(loadFile == null) {
            loadFile = file;
        }
    }

    public void calcBoundsAndTransform() {
        projection.toIdentity().perspective(fovDegrees, Game.getInstance().aspectRatio(), zNear, zFar);
        view.toIdentity().lookAt(eye, target, up);
        root.calcBoundsAndTransform();
    }

    public void rotateAroundTarget(float dx, float dy) {
        eye.sub(target, f);
        m.toIdentity().rotate(dx, 0, 1, 0);
        m.transformNormal(f.cross(up, r)).normalize();
        m.transformNormal(f);
        m.toIdentity().rotate(dy, r);
        m.transformNormal(r.cross(f, up)).normalize();
        m.transformNormal(f);
        target.add(f, eye);
    }

    public void rotateAroundEye(float dx, float dy) {
        target.sub(eye, f).normalize();
        m.toIdentity().rotate(dx, 0, 1, 0);
        m.transformNormal(f.cross(up, r)).normalize();
        m.transformNormal(f).normalize();
        m.toIdentity().rotate(dy, r);
        m.transformNormal(r.cross(f, up)).normalize();
        m.transformNormal(f).normalize();
        eye.add(f, target);
    }

    public void move(Vec3 point, float dx, float dy) {
        move(point, dx, dy, null);
    }

    public void move(Vec3 point, float dx, float dy, Mat4 transform) {
        float dl = (float)Math.sqrt(dx * dx + dy * dy);

        eye.sub(target, f);
        d.set(f);
        f.negate();
        f.y = 0;
        if(f.length() > 0.0000001 && dl > 0.0001) {
            f.normalize().cross(u.set(0, 1, 0), r).normalize();
            f.scale(dy).add(r.scale(dx));
            if(transform != null) {
                transform.transformNormal(f);
            }
            point.add(f);
        }
        target.add(d, eye);
    }

    public void move(Vec3 point, float dy) {
        move(point, dy, null);
    }

    public void move(Vec3 point, float dy, Mat4 transform) {
        eye.sub(target, d);
        u.set(0, dy, 0);
        if(transform != null) {
            transform.transformNormal(u);
        }
        point.add(u);
        target.add(d, eye);
    }

    public void zoom(float amount) {
        eye.sub(target, d);
        d.normalize(d.length() + amount);
        target.add(d, eye);
    }


    boolean isectPoint(float cx, float cy, float cz, float halfSize, float[] time) {
        int w = Game.getInstance().w();
        int h = Game.getInstance().h();
        int x = Game.getInstance().mouseX();
        int y = h - Game.getInstance().mouseY() - 1;

        GFX.unproject(x, y, 0, 0, 0, w, h, projection, view, origin);
        GFX.unproject(x, y, 1, 0, 0, w, h, projection, view, direction);

        direction.sub(origin).normalize();

        bounds.min.set(cx, cy, cz).sub(halfSize, halfSize, halfSize);
        bounds.max.set(cx, cy, cz).add(halfSize, halfSize, halfSize);

        if(bounds.isects(origin, direction, time)) {
            return true;
        }
        return false;
    }

    static Scene next(Scene scene) {
        File f = scene.getLoadFile();
        Game game = Game.getInstance();

        if(f != null) {
            try {
                scene = null;
                game.getAssets().clear();
                scene = SceneSerializer.deserialize(false, f);
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        }
        return scene;
    }
}

