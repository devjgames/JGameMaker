package org.game;

public final class Triangle {
    
    public final Vec3 p1 = new Vec3();
    public final Vec3 p2 = new Vec3();
    public final Vec3 p3 = new Vec3();
    public final Vec3 n = new Vec3();
    public float d  = 0;
    public int tag = 0;

    private final Vec3 n2 = new Vec3();
    private final Vec3 v = new Vec3();
    private final Vec3 a = new Vec3();
    private final Vec3 b = new Vec3();
    private final Vec3 p = new Vec3();
    private final Vec3 c = new Vec3();
    private final Vec3 ab = new Vec3();
    private final Vec3 ap = new Vec3();

    public Triangle() {
    }

    public Vec3 getPoint(int i) {
        if(i == 1) {
            return p2;
        } else if(i == 2) {
            return p3;
        } else {
            return p1;
        }
    }

    public Triangle setTag(int tag) {
        this.tag = tag;

        return this;
    }

    public Triangle set(Triangle triangle) {
        p1.set(triangle.p1);
        p2.set(triangle.p2);
        p3.set(triangle.p3);
        n.set(triangle.n);
        d = triangle.d;
        tag = triangle.tag;

        return this;
    }

    public Triangle calcPlane() {
        p2.sub(p1, a);
        p3.sub(p2, b);
        a.cross(b, n).normalize();

        d = -p1.dot(n);

        return this;
    }

    public Triangle transform(Mat4 m) {
        m.transform(p1);
        m.transform(p2);
        m.transform(p3);

        return calcPlane();
    }

    public boolean contains(Vec3 point, float buffer) {
        for(int i = 0; i != 3; i++) {
            a.set(getPoint(i));
            b.set(getPoint(i + 1));
            b.sub(a, v);
            n.cross(v, n2).normalize(-buffer);
            a.add(n2);
            n.cross(v, n2).normalize();

            float d2 = -a.dot(n2);
            float s = n2.dot(point) + d2;

            if(s < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean intersectsPlane(Vec3 origin, Vec3 direction, float[] time) {
        float t = direction.dot(n);

        if(Math.abs(t) > 0.0000001) {
            t = (-d - origin.dot(n)) / t;
            if(t > 0.0000001 && t < time[0]) {
                time[0] = t;
                return true;
            }
        }
        return false;
    }

    public boolean intersects(Vec3 origin, Vec3 direction, float buffer, float[] time) {
        float t = time[0];

        if(intersectsPlane(origin, direction, time)) {
            direction.scale(time[0], p).add(origin);
            if(contains(p, buffer)) {
                return true;
            }
            time[0] = t;
        }
        return false;
    }

    public Vec3 closestPoint(Vec3 point, Vec3 closestPoint) {
        float min = Float.MAX_VALUE;

        for(int i = 0; i != 3; i++) {
            a.set(getPoint(i));
            b.set(getPoint(i + 1));
            b.sub(a, ab);
            point.sub(a, ap);

            float s = ab.dot(ap);

            c.set(a);
            if(s > 0) {
                s /= ab.lengthSquared();
                if(s > 1) {
                    c.set(b);
                } else {
                    a.add(ab.scale(s), c);
                }
            }
            point.sub(c, v);

            float l = v.length();

            if(l < min) {
                min = l;
                closestPoint.set(c);
            }
        }
        return closestPoint;
    }

    @Override
    public String toString() {
        return p1 + " : " + p2 + " : " + p3 + " : " + n + " : " + d + " @ " + tag;
    }
}
