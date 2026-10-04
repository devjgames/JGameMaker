package org.game;

public final class Vec2 {
   
    public float x;
    public float y;

    public Vec2() {
        toZero();
    }

    public Vec2(float x, float y) {
        set(x, y);
    }

    public Vec2(Vec2 v) {
        set(v);
    }

    public float component(int i) {
        if(i == 0) {
            return x;
        } else {
            return y;
        }
    }

    public void setComponent(int i, float value) {
        if(i == 0) {
            x = value;
        } else {
            y = value;
        } 
    }

    public float lengthSquared() {
        return x * x + y * y ;
    }

    public float length() {
        return (float)Math.sqrt(lengthSquared());
    }

    public float distance(float x, float y) {
        float dx = x - this.x;
        float dy = y - this.y;
        
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public float distance(Vec2 v) {
        return distance(v.x, v.y);
    }

    public float dot(float x, float y) {
        return this.x * x + this.y * y;
    }

    public float dot(Vec2 v) {
        return dot(v.x, v.y);
    }

    public Vec2 normalize(float length) {
        return div(length()).scale(length);
    }

    public Vec2 normalize() {
        return normalize(1);
    }

    public Vec2 normalize(float length, Vec2 dest) {
        return dest.set(this).normalize(length);
    }

    public Vec2 normalize(Vec2 dest) {
        return normalize(1, dest);
    }

    public Vec2 lerp(Vec2 v, float amount, Vec2 dest) {
        dest.x = x + amount * (v.x - x);
        dest.y = y + amount * (v.y - y);

        return dest;
    }

    public Vec2 lerp(Vec2 v, float amount) {
        return lerp(v, amount, this);
    }

    public Vec2 toZero() {
        x = 0;
        y = 0;

        return this;
    }

    public Vec2 set(float x, float y) {
        this.x = x;
        this.y = y;

        return this;
    }

    public Vec2 set(Vec2 v) {
        x = v.x;
        y = v.y;

        return this;
    }

    public Vec2 add(float x, float y, Vec2 dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;

        return dest;
    }

    public Vec2 add(float x, float y) {
        return add(x, y, this);
    }

    public Vec2 add(Vec2 v) {
        return add(v, this);
    }

    public Vec2 add(Vec2 v, Vec2 dest) {
        return add(v.x, v.y, dest);
    }

    public Vec2 sub(float x, float y, Vec2 dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;

        return dest;
    }

    public Vec2 sub(float x, float y) {
        return sub(x, y, this);
    }

    public Vec2 sub(Vec2 v) {
        return sub(v, this);
    }

    public Vec2 sub(Vec2 v, Vec2 dest) {
        return sub(v.x, v.y, dest);
    }

    public Vec2 negate(Vec2 dest) {
        dest.x = -x;
        dest.y = -y;

        return dest;
    }

    public Vec2 negate() {
        return negate(this);
    }

    public Vec2 scale(float x, float y, Vec2 dest) {
        dest.x = this.x * x;
        dest.y = this.y * y;

        return dest;
    }

    public Vec2 scale(float x, float y) {
        return scale(x, y, this);
    }

    public Vec2 scale(Vec2 v) {
        return scale(v.x, v.y);
    }

    public Vec2 scale(float s) {
        return scale(s, s);
    }

    public Vec2 scale(float s, Vec2 dest) {
        return scale(s, s, dest);
    }

    public Vec2 div(float x, float y, Vec2 dest) {
        dest.x = this.x / x;
        dest.y = this.y / y;

        return dest;
    }

    public Vec2 div(float x, float y) {
        return div(x, y, this);
    }

    public Vec2 div(Vec2 v) {
        return div(v.x, v.y);
    }

    public Vec2 div(Vec2 v, Vec2 dest) {
        return div(v.x, v.y, dest);
    }

    public Vec2 div(float d) {
        return div(d, d);
    }

    public Vec2 div(float d, Vec2 dest) {
        return div(d, d, dest);
    }

    @Override
    public String toString() {
        return x + " " + y;
    }

    public static Vec2 parse(String s, Vec2 v) {
        String[] tokens = s.split("\\s+");

        if(tokens.length >= 2) {
            try {
                float x = Float.parseFloat(tokens[0]);
                float y = Float.parseFloat(tokens[1]);

                v.set(x, y);
            } catch(NumberFormatException ex) {
            }
        }
        return v;
    }
}
