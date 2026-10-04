package org.game;

import java.util.Vector;

public final class Collider {
    
    public float radius = 16;
    public int groundSlope = 60;
    public int roofSlope = 50;
    public final Vec3 velocity = new Vec3();

    private int tested = 0;
    private final Vec3 f = new Vec3();
    private final Vec3 u = new Vec3();
    private final Vec3 r = new Vec3();
    private final Vec3 o = new Vec3();
    private final Vec3 d = new Vec3();
    private final Vec3 p = new Vec3();
    private final Vec3 c = new Vec3();
    private final AABB bounds = new AABB();
    private final Triangle triangle = new Triangle();
    private boolean onGround = false;
    private boolean hitRoof = false;
    private final Mat4 groundMatrix = new Mat4();
    private final Vec3 delta = new Vec3();
    private final Vec3 hNormal = new Vec3();
    private final Vec3 groundNormal = new Vec3();
    private final Triangle hTriangle = new Triangle();
    private SceneNode hNode = null;
    private final Vec3 rPosition = new Vec3();
    private final float[] time = new float[1];
    private final Vector<SceneNode> hitNodes = new Vector<>();

    public int getHitNodeCount() {
        return hitNodes.size();
    }

    public SceneNode getHitNode(int i) {
        return hitNodes.get(i);
    }

    public boolean isOnGround() {
        return onGround;
    }

    public boolean didHitRoof() {
        return hitRoof;
    }

    public int getTested() {
        return tested;
    }

    public boolean intersect(Scene scene, SceneNode root, Vec3 origin, Vec3 direction, float buffer, int mask, float[] time, boolean ignoreBackfaces, Triangle hit) throws Exception {
        hNode = null;
        root.traverse((n) -> {
            bounds.clear();
            bounds.add(origin);
            bounds.add(direction.scale(time[0], f).add(origin));
            if(n.bounds.touches(bounds)) {
                if(n.collidable) {
                    OctTree octTree = n.getOctTree(scene);

                    if(octTree != null) {
                        octTree.traverse((t) -> {
                            if(t.getBounds().touches(bounds)) {
                                for(int i = 0; i != t.getTriangleCount(); i++) {
                                    t.getTriangle(i, triangle);
                                    if((triangle.tag & mask) != 0) {
                                        boolean skip = ignoreBackfaces;
                                        
                                        if(skip) {
                                            skip = triangle.n.dot(direction) > 0;
                                        }
                                        if(!skip) {
                                            if(triangle.intersects(origin, direction, buffer, time)) {;
                                                hit.set(triangle);
                                                hNode = n;
                                            }
                                        }
                                    }
                                }
                                return true;
                            }
                            return false;
                        });
                    } else {
                        for(int i = 0; i != n.getTriangleCount(); i++) {
                            n.getTriangle(scene, i, triangle);
                            if((triangle.tag & mask) != 0) {
                                boolean skip = ignoreBackfaces;
                                        
                                if(skip) {
                                    skip = triangle.n.dot(direction) > 0;
                                }
                                if(!skip) {
                                    if(triangle.intersects(origin, direction, buffer, time)) {
                                        hit.set(triangle);
                                        hNode = n;
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            }
            return false;
        });
        hitNodes.clear();
        if(hNode != null) {
            hitNodes.add(hNode);
        }
        return hNode != null;
    }

    public boolean resolve(Scene scene, SceneNode root, Vec3 position) throws Exception {
        boolean collided = false;

        hitNodes.clear();

        velocity.scale(Game.getInstance().elapsedTime(), delta);
        if(delta.length() > radius * 0.5f) {
            delta.normalize(radius * 0.5f);
        }
        groundMatrix.transformNormal(delta);
        position.add(delta);
        groundMatrix.toIdentity();
        onGround = false;
        groundNormal.toZero();
        tested = 0;
        for(int i = 0; i != 3; i++) {
            hNode = null;
            hNormal.toZero();
            bounds.min.set(position).sub(radius, radius, radius);
            bounds.max.set(position).add(radius, radius, radius);
            time[0] = radius;
            root.traverse((n) -> {
                if(n.bounds.touches(bounds)) {
                    if(n.collidable) {
                        OctTree tree = n.getOctTree(scene);

                        if(tree != null) {
                            tree.traverse((t) -> {
                                if(t.getBounds().touches(bounds)) {
                                    for(int j = 0; j != t.getTriangleCount(); j++) {
                                        if(resolve(t.getTriangle(j, triangle), position, hNormal, rPosition)) {
                                            hNode = n;
                                        }
                                    }
                                    return true;
                                }
                                return false;
                            });
                        } else {
                            for(int j = 0; j != n.getTriangleCount(); j++) {
                                if(resolve(n.getTriangle(scene, j, triangle), position, hNormal, rPosition)) {
                                    hNode = n;
                                }
                            }
                        }
                    }
                    return true;
                }
                return false;
            });
            if(hNode != null) {
                if(Math.acos(Math.max(-0.999f, Math.min(0.999f, hNormal.dot(0, 1, 0)))) < Math.toRadians(groundSlope)) {
                    groundNormal.add(hNormal);
                    onGround = true;
                    velocity.y = 0;
                }
                if(Math.acos(Math.max(-0.999f, Math.min(0.999f, hNormal.dot(0, -1, 0)))) < Math.toRadians(roofSlope)) {
                    hitRoof = true;
                    velocity.y = 0;
                }
                position.set(rPosition);
                collided = true;
                hitNodes.add(hNode);
            } else {
                break;
            }
        }
        if(onGround) {
            groundNormal.normalize(u);
            r.set(1, 0, 0);
            r.cross(u, f).normalize();
            u.cross(f, r).normalize();
            groundMatrix.set(
                r.x, u.x, f.x, 0,
                r.y, u.y, f.y, 0,
                r.z, u.z, f.z, 0,
                0, 0, 0, 1
            );
        }
        return collided;
    }

    private boolean resolve(Triangle triangle, Vec3 position, Vec3 hNormal, Vec3 rPosition) {
        float t = time[0];

        triangle.n.negate(d);
        o.set(position);
        if(triangle.intersectsPlane(o, d, time)) {
            p.set(d).scale(time[0]);
            o.add(p, p);
            if(triangle.contains(p, 0)) {
                hNormal.set(triangle.n);
                hTriangle.set(triangle);
                p.add(rPosition.set(hNormal).scale(radius), rPosition);
                return true;
            } else {
                time[0] = t;
                triangle.closestPoint(o, c);
                o.sub(c, d);
                if(d.length() > 0.0000001 && d.length() < time[0]) {
                    time[0] = d.length();
                    d.normalize(hNormal);
                    hTriangle.set(triangle);
                    c.add(rPosition.set(hNormal).scale(radius), rPosition);
                    return true;
                }
            }
        }
        tested++;

        return false;
    }
}
