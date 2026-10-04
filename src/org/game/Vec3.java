package org.game;

public final class Vec3 {
   
    public float x;
    public float y;
    public float z;

    public Vec3() {
        toZero();
    }

    public Vec3(float x, float y, float z) {
        set(x, y, z);
    }

    public Vec3(Vec2 v, float z) {
        set(v, z);
    }

    public Vec3(Vec3 v) {
        set(v);
    }

    public float component(int i) {
        if(i == 0) {
            return x;
        } else if(i == 1) {
            return y;
        } else {
            return z;
        } 
    }

    public void setComponent(int i, float value) {
        if(i == 0) {
            x = value;
        } else if(i == 1) {
            y = value;
        } else {
            z = value;
        }
    }

    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public float length() {
        return (float)Math.sqrt(lengthSquared());
    }

    public float distance(float x, float y, float z) {
        float dx = x - this.x;
        float dy = y - this.y;
        float dz = z - this.z;
        
        return (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public float distance(Vec3 v) {
        return distance(v.x, v.y, v.z);
    }

    public float dot(float x, float y, float z) {
        return this.x * x + this.y * y + this.z * z;
    }

    public float dot(Vec3 v) {
        return dot(v.x, v.y, v.z);
    }

    public Vec3 normalize(float length) {
        return div(length()).scale(length);
    }

    public Vec3 normalize() {
        return normalize(1);
    }

    public Vec3 normalize(float length, Vec3 dest) {
        return dest.set(this).normalize(length);
    }

    public Vec3 normalize(Vec3 dest) {
        return normalize(1, dest);
    }

    public Vec3 lerp(Vec3 v, float amount, Vec3 dest) {
        dest.x = x + amount * (v.x - x);
        dest.y = y + amount * (v.y - y);
        dest.z = z + amount * (v.z - z);

        return dest;
    }

    public Vec3 lerp(Vec3 v, float amount) {
        return lerp(v, amount, this);
    }

    public Vec3 cross(Vec3 v, Vec3 dest) {
        float cx = y * v.z - z * v.y;
        float cy = z * v.x - x * v.z;
        float cz = x * v.y - y * v.x;

        return dest.set(cx, cy, cz);
    }

    public Vec3 toZero() {
        x = 0;
        y = 0;
        z = 0;

        return this;
    }

    public Vec3 set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    public Vec3 set(Vec2 v, float z) {
        this.x = v.x;
        this.y = v.y;
        this.z = z;

        return this;
    }

    public Vec3 set(Vec3 v) {
        x = v.x;
        y = v.y;
        z = v.z;

        return this;
    }

    public Vec3 add(float x, float y, float z, Vec3 dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        dest.z = this.z + z;

        return dest;
    }

    public Vec3 add(float x, float y, float z) {
        return add(x, y, z, this);
    }

    public Vec3 add(Vec3 v) {
        return add(v, this);
    }

    public Vec3 add(Vec3 v, Vec3 dest) {
        return add(v.x, v.y, v.z, dest);
    }

    public Vec3 sub(float x, float y, float z, Vec3 dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        dest.z = this.z - z;

        return dest;
    }

    public Vec3 sub(float x, float y, float z) {
        return sub(x, y, z, this);
    }

    public Vec3 sub(Vec3 v) {
        return sub(v, this);
    }

    public Vec3 sub(Vec3 v, Vec3 dest) {
        return sub(v.x, v.y, v.z, dest);
    }

    public Vec3 negate(Vec3 dest) {
        dest.x = -x;
        dest.y = -y;
        dest.z = -z;

        return dest;
    }

    public Vec3 negate() {
        return negate(this);
    }

    public Vec3 scale(float x, float y, float z, Vec3 dest) {
        dest.x = this.x * x;
        dest.y = this.y * y;
        dest.z = this.z * z;

        return dest;
    }

    public Vec3 scale(float x, float y, float z) {
        return scale(x, y, z, this);
    }

    public Vec3 scale(Vec3 v) {
        return scale(v.x, v.y, v.z);
    }

    public Vec3 scale(float s) {
        return scale(s, s, s);
    }

    public Vec3 scale(float s, Vec3 dest) {
        return scale(s, s, s, dest);
    }

    public Vec3 div(float x, float y, float z, Vec3 dest) {
        dest.x = this.x / x;
        dest.y = this.y / y;
        dest.z = this.z / z;

        return dest;
    }

    public Vec3 div(float x, float y, float z) {
        return div(x, y, z, this);
    }

    public Vec3 div(Vec3 v) {
        return div(v.x, v.y, v.z);
    }

    public Vec3 div(Vec3 v, Vec3 dest) {
        return div(v.x, v.y, v.z, dest);
    }

    public Vec3 div(float d) {
        return div(d, d, d);
    }

    public Vec3 div(float d, Vec3 dest) {
        return div(d, d, d, dest);
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }

    public static Vec3 parse(String s, Vec3 v) {
        String[] tokens = s.split("\\s+");

        if(tokens.length >= 3) {
            try {
                float x = Float.parseFloat(tokens[0]);
                float y = Float.parseFloat(tokens[1]);
                float z = Float.parseFloat(tokens[2]);

                v.set(x, y, z);
            } catch(NumberFormatException ex) {
            }
        }
        return v;
    }
}
