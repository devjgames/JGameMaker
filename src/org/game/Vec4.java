package org.game;

public final class Vec4 {
    
    public float x;
    public float y;
    public float z;
    public float w;

    public Vec4() {
        toZero();
    }

    public Vec4(float x, float y, float z, float w) {
        set(x, y, z, w);
    }

    public Vec4(Vec2 v, float z, float w) {
        set(v, z, w);
    }

    public Vec4(Vec3 v, float w) {
        set(v, w);
    }

    public Vec4(Vec4 v) {
        set(v);
    }

    public float component(int i) {
        if(i == 0) {
            return x;
        } else if(i == 1) {
            return y;
        } else if(i == 2) {
            return z;
        } else {
            return w;
        }
    }

    public void setComponent(int i, float value) {
        if(i == 0) {
            x = value;
        } else if(i == 1) {
            y = value;
        } else if(i == 2) {
            z = value;
        } else {
            w = value;
        }
    }

    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }

    public float length() {
        return (float)Math.sqrt(lengthSquared());
    }

    public float distance(float x, float y, float z, float w) {
        float dx = x - this.x;
        float dy = y - this.y;
        float dz = z - this.z;
        float dw = w - this.w;
        
        return (float)Math.sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
    }

    public float distance(Vec4 v) {
        return distance(v.x, v.y, v.z, v.w);
    }

    public float dot(float x, float y, float z, float w) {
        return this.x * x + this.y * y + this.z * z + this.w * w;
    }

    public float dot(Vec4 v) {
        return dot(v.x, v.y, v.z, v.w);
    }

    public Vec4 normalize(float length) {
        return div(length()).scale(length);
    }

    public Vec4 normalize() {
        return normalize(1);
    }

    public Vec4 normalize(float length, Vec4 dest) {
        return dest.set(this).normalize(length);
    }

    public Vec4 normalize(Vec4 dest) {
        return normalize(1, dest);
    }

    public Vec4 lerp(Vec4 v, float amount, Vec4 dest) {
        dest.x = x + amount * (v.x - x);
        dest.y = y + amount * (v.y - y);
        dest.z = z + amount * (v.z - z);
        dest.w = w + amount * (v.w - w);

        return dest;
    }

    public Vec4 lerp(Vec4 v, float amount) {
        return lerp(v, amount, this);
    }

    public Vec4 toZero() {
        x = 0;
        y = 0;
        z = 0;
        w = 0;

        return this;
    }

    public Vec4 set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;

        return this;
    }

    public Vec4 set(Vec2 v, float z, float w) {
        this.x = v.x;
        this.y = v.y;
        this.z = z;
        this.w = w;

        return this;
    }

    public Vec4 set(Vec3 v, float w) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.w = w;

        return this;
    }

    public Vec4 set(Vec4 v) {
        x = v.x;
        y = v.y;
        z = v.z;
        w = v.w;

        return this;
    }

    public Vec4 add(float x, float y, float z, float w, Vec4 dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        dest.z = this.z + z;
        dest.w = this.w + w;

        return dest;
    }

    public Vec4 add(float x, float y, float z, float w) {
        return add(x, y, z, w, this);
    }

    public Vec4 add(Vec4 v) {
        return add(v, this);
    }

    public Vec4 add(Vec4 v, Vec4 dest) {
        return add(v.x, v.y, v.z, v.w, dest);
    }

    public Vec4 sub(float x, float y, float z, float w, Vec4 dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        dest.z = this.z - z;
        dest.w = this.w - w;

        return dest;
    }

    public Vec4 sub(float x, float y, float z, float w) {
        return sub(x, y, z, w, this);
    }

    public Vec4 sub(Vec4 v) {
        return sub(v, this);
    }

    public Vec4 sub(Vec4 v, Vec4 dest) {
        return sub(v.x, v.y, v.z, v.w, dest);
    }

    public Vec4 negate(Vec4 dest) {
        dest.x = -x;
        dest.y = -y;
        dest.z = -z;
        dest.w = -w;

        return dest;
    }

    public Vec4 negate() {
        return negate(this);
    }

    public Vec4 scale(float x, float y, float z, float w, Vec4 dest) {
        dest.x = this.x * x;
        dest.y = this.y * y;
        dest.z = this.z * z;
        dest.w = this.w * w;

        return dest;
    }

    public Vec4 scale(float x, float y, float z, float w) {
        return scale(x, y, z, w, this);
    }

    public Vec4 scale(Vec4 v) {
        return scale(v.x, v.y, v.z, v.w);
    }

    public Vec4 scale(float s) {
        return scale(s, s, s, s);
    }

    public Vec4 scale(float s, Vec4 dest) {
        return scale(s, s, s, s, dest);
    }

    public Vec4 div(float x, float y, float z, float w, Vec4 dest) {
        dest.x = this.x / x;
        dest.y = this.y / y;
        dest.z = this.z / z;
        dest.w = this.w / w;

        return dest;
    }

    public Vec4 div(float x, float y, float z, float w) {
        return div(x, y, z, w, this);
    }

    public Vec4 div(Vec4 v) {
        return div(v.x, v.y, v.z, v.w);
    }

    public Vec4 div(Vec4 v, Vec4 dest) {
        return div(v.x, v.y, v.z, v.w, dest);
    }

    public Vec4 div(float d) {
        return div(d, d, d, d);
    }

    public Vec4 div(float d, Vec4 dest) {
        return div(d, d, d, d, dest);
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z + " " + w;
    }

    public static Vec4 parse(String s, Vec4 v) {
        String[] tokens = s.split("\\s+");

        if(tokens.length >= 4) {
            try {
                float x = Float.parseFloat(tokens[0]);
                float y = Float.parseFloat(tokens[1]);
                float z = Float.parseFloat(tokens[2]);
                float w = Float.parseFloat(tokens[3]);

                v.set(x, y, z, w);
            } catch(NumberFormatException ex) {
            }
        }
        return v;
    }
}
