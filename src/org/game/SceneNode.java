package org.game;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public final class SceneNode implements Iterable<SceneNode> {

    public static interface Visitor {
        boolean visit(SceneNode node) throws Exception;
    }
    
    public String name = "Node";
    public boolean visible = true;
    public boolean collidable = false;
    public boolean dynamic = false;
    public boolean emitsLight = false;
    public boolean receivesLight = true;
    public boolean isLocation = false;
    public boolean drawLines = false;
    public boolean drawTreeLines = false;
    public boolean isBrush = false;
    public final Vec3 position = new Vec3();
    @Hidden
    public final Vec3 absolutePosition = new Vec3();
    public final Vec3 r = new Vec3(1, 0, 0);
    public final Vec3 u = new Vec3(0, 1, 0);
    public final Vec3 f = new Vec3(0, 0, 1);
    public final Vec3 scale = new Vec3(1, 1, 1);
    public final AABB bounds = new AABB();
    public final Mat4 localModel = new Mat4();
    public final Mat4 model = new Mat4();
    public final Mat4 modelIT = new Mat4();
    public final Vec4 ambientColor = new Vec4(0.15f, 0.15f, 0.15f, 1);
    public final Vec4 diffuseColor = new Vec4(1, 1, 1, 1);
    public final Vec4 specularColor = new Vec4(0, 0, 0, 1);
    public float specularPower = 64;
    public final Vec4 lightColor = new Vec4(1, 1, 1, 1);
    public float lightRadius = 300;
    public DepthState depthState = DepthState.READWRITE;
    public CullState cullState = CullState.BACK;
    public BlendState blendState = BlendState.OPAQUE;
    public int zOrder = 0;
    public int minTrisPerTree = 16;
    public int triangleTag = 1;
    public Renderable renderable = null;
    public boolean warpEnabled = false;
    public float warpAmplitude = 8;
    public float warpFrequency = 0.05f;
    public float warpSpeed = 2;
    public boolean warpY = false;
    public final Hashtable<String, Object> properties = new Hashtable<>();

    private final Vector<SceneNode> children = new Vector<>();
    private SceneNode parent = null;
    private final Mat4 m = new Mat4();
    private OctTree octTree = null;

    private final Vector<SceneNodeComponent> components = new Vector<>();

    public SceneNode() {
    }

    public SceneNode(Scene scene, SceneNode node) throws Exception {
        if(node.renderable != null) {
            renderable = node.renderable.newInstance();
        }
        Utils.copy(node, this);
        for(SceneNode child : node) {
            addChild(new SceneNode(scene, child));
        }
        for(SceneNodeComponent component : node.components) {
            component.newInstance(scene, this);
        }
    }

    public int getComponentCount() {
        return components.size();
    }

    public SceneNodeComponent getComponent(int i) {
        return components.get(i);
    }

    void addComponent(SceneNodeComponent component) {
        components.add(component);
    }

    void clearComponents() {
        while(!components.isEmpty()) {
            removeComponent(components.get(0));
        }
    }

    void removeComponent(SceneNodeComponent component) {
        components.remove(component);
    }

     public int getTriangleCount() {
        if(renderable != null) {
            return renderable.getTriangleCount();
        }
        return 0;
    }

    public Triangle getTriangle(Scene scene, int i, Triangle triangle) {
        if(renderable != null) {
            return renderable.getTriangle(scene, this, i, triangle).transform(model).setTag(triangleTag);
        }
        return null;
    }

    public OctTree getOctTree(Scene scene) {
        if(octTree == null && !dynamic && renderable != null) {
            Vector<Triangle> triangles = new Vector<>();

            for(int i = 0; i != getTriangleCount(); i++) {
                triangles.add(getTriangle(scene, i, new Triangle()));
            }
            octTree = OctTree.create(triangles, minTrisPerTree);
        }
        return octTree;
    }

    public void clearOctTree() {
        octTree = null;
    }

    public int getChildCount() {
        return children.size();
    }

    public SceneNode getChild(int i) {
        return children.get(i);
    }
    
    public SceneNode getRoot() {
        SceneNode root = this;

        while(root.parent != null) {
            root = root.parent;
        }
        return root;
    }

    public SceneNode getParent() {
        return parent;
    }

    public void detach() {
        if(parent != null) {
            parent.children.remove(this);
            parent = null;
        }
    }

    public void addChild(SceneNode node) {
        node.detach();
        node.parent = this;
        children.add(node);
    }

    public void detachAllChildren() {
        while(!children.isEmpty()) {
            children.firstElement().detach();
        }
    }

    public void calcBoundsAndTransform() {
        localModel
            .toIdentity()
            .translate(position)
            .mul(
                m.set(
                    r.x, u.x, f.x, 0,
                    r.y, u.y, f.y, 0,
                    r.z, u.z, f.z, 0,
                    0, 0, 0, 1
                )
            )
            .scale(scale);
        model.set(localModel);
        if(parent != null) {
            model.set(parent.model).mul(localModel);
        }
        modelIT.set(model).invert().transpose();
        model.transform(absolutePosition.toZero());
        bounds.clear();
        if(renderable != null) {
            bounds.set(renderable.getBounds()).transform(model);
        } 
        if(emitsLight) {
            Vec3 p = absolutePosition;
            float r = lightRadius;

            bounds.add(p.x - r, p.y - r, p.z - r);
            bounds.add(p.x + r, p.y + r, p.z + r);
        }
        for(SceneNode node : this) {
            node.calcBoundsAndTransform();
            bounds.add(node.bounds);
        }
    }

    public void rotate(int axis, float degrees) {
        if(axis == 0) {
            m.toIdentity().rotate(degrees, r);
            m.transformNormal(u).normalize();
            m.transformNormal(f).normalize();
        } else if(axis == 1) {
            m.toIdentity().rotate(degrees, u);
            m.transformNormal(r).normalize();
            m.transformNormal(f).normalize();
        } else {
            m.toIdentity().rotate(degrees, f);
            m.transformNormal(r).normalize();
            m.transformNormal(u).normalize();
        }
    }

    public boolean move(Vec3 p1, Vec3 p2, float speed) {
        float dx = p2.x - p1.x;
        float dy = p2.y - p1.y;
        float dz = p2.z - p1.z;
        float len = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
        float nx = dx / len;
        float ny = dy / len;
        float nz = dz / len;
        float vl = Game.getInstance().elapsedTime() * speed;

        position.add(nx * vl, ny * vl, nz * vl);

        float vx = position.x - p1.x;
        float vy = position.y - p1.y;
        float vz = position.z - p1.z;
        float s = vx * nx + vy * ny + vz * nz;

        r.set(nx, ny, nz);
        u.set(0, 1, 0);
        r.cross(u, f).normalize();
        f.cross(r, u).normalize();

        if(s >= len) {
            position.set(p2);
            return true;
        }
        return false;
    }

    public void traverse(Visitor v) throws Exception {
        if(v.visit(this)) {
            for(SceneNode node : this) {
                node.traverse(v);
            }
        }
    }

    public SceneNode find(Visitor v) throws Exception {
        if(v.visit(this)) {
            return this;
        }
        for(SceneNode node : this) {
            SceneNode r = node.find(v);

            if(r != null) {
                return r;
            }
        }
        return null;
    }

    void addBrushes(Scene scene, Vector<SceneNode> brushes) throws Exception {
        if(isBrush) {
            brushes.add(new SceneNode(scene, this));
        } else {
            for(SceneNode node : this) {
                node.addBrushes(scene, brushes);
            }
        }
    }

    void init() {
        for(SceneNodeComponent component : components) {
            if(!component.isComplete) {
                try {
                    component.init();
                } catch(Exception ex) {
                    Log.put(0, ex);
                }
            }
        }
    }

    void start() {
        for(SceneNodeComponent component : components) {
            if(!component.isComplete) {
                try {
                    component.start();
                } catch(Exception ex) {
                    Log.put(0, ex);
                } finally {
                    component.isComplete = true;
                }
            }
        }
    }

    void update(Scene scene) {
        for(SceneNodeComponent component : components) {
            try {
                component.update();
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        }
        if(renderable != null) {
            try {
                renderable.update(scene, this);
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        }
    }

    void renderSprites(SpriteRenderer renderer) {
        for(SceneNodeComponent component : components) {
            try {
                component.renderSprites(renderer); 
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        }
    }
    
    void reloadComponents(Scene scene) {
        Vector<SceneNodeComponent> old = new Vector<>(components);

        components.clear();
        for(int i = 0; i != old.size(); i++) {
            old.get(i).newInstance(scene, this);
        }
        for(SceneNode node : children) {
            node.reloadComponents(scene);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Iterator<SceneNode> iterator() {
        return children.iterator();
    }
}
