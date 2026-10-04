package org.game;

public final class Mat4 {
    
    public float m00, m01, m02, m03;
    public float m10, m11, m12, m13;
    public float m20, m21, m22, m23;
    public float m30, m31, m32, m33;

    public Mat4() {
        toIdentity();
    }

    public Mat4(
        float m00, float m01, float m02, float m03,
        float m10, float m11, float m12, float m13,
        float m20, float m21, float m22, float m23,
        float m30, float m31, float m32, float m33
    ) {
        set(
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33
        );
    }

    public Mat4(Mat4 m) {
        set(m);
    }

    public Mat4 set(
        float m00, float m01, float m02, float m03,
        float m10, float m11, float m12, float m13,
        float m20, float m21, float m22, float m23,
        float m30, float m31, float m32, float m33
    ) {
        this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
        this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
        this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
        this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;

        return this;
    }

    public Mat4 set(Mat4 m) {
        return set( 
            m.m00, m.m01, m.m02, m.m03,
            m.m10, m.m11, m.m12, m.m13,
            m.m20, m.m21, m.m22, m.m23,
            m.m30, m.m31, m.m32, m.m33
        );
    }

    public Mat4 mul(
        float m00, float m01, float m02, float m03,
        float m10, float m11, float m12, float m13,
        float m20, float m21, float m22, float m23,
        float m30, float m31, float m32, float m33
    ) {
        return set( 
            this.m00 * m00 + this.m01 * m10 + this.m02 * m20 + this.m03 * m30,
            this.m00 * m01 + this.m01 * m11 + this.m02 * m21 + this.m03 * m31,
            this.m00 * m02 + this.m01 * m12 + this.m02 * m22 + this.m03 * m32,
            this.m00 * m03 + this.m01 * m13 + this.m02 * m23 + this.m03 * m33,

            this.m10 * m00 + this.m11 * m10 + this.m12 * m20 + this.m13 * m30,
            this.m10 * m01 + this.m11 * m11 + this.m12 * m21 + this.m13 * m31,
            this.m10 * m02 + this.m11 * m12 + this.m12 * m22 + this.m13 * m32,
            this.m10 * m03 + this.m11 * m13 + this.m12 * m23 + this.m13 * m33,

            this.m20 * m00 + this.m21 * m10 + this.m22 * m20 + this.m23 * m30,
            this.m20 * m01 + this.m21 * m11 + this.m22 * m21 + this.m23 * m31,
            this.m20 * m02 + this.m21 * m12 + this.m22 * m22 + this.m23 * m32,
            this.m20 * m03 + this.m21 * m13 + this.m22 * m23 + this.m23 * m33,

            this.m30 * m00 + this.m31 * m10 + this.m32 * m20 + this.m33 * m30,
            this.m30 * m01 + this.m31 * m11 + this.m32 * m21 + this.m33 * m31,
            this.m30 * m02 + this.m31 * m12 + this.m32 * m22 + this.m33 * m32,
            this.m30 * m03 + this.m31 * m13 + this.m32 * m23 + this.m33 * m33
        );
    }

    public Mat4 mul(Mat4 m) {
        return mul( 
            m.m00, m.m01, m.m02, m.m03,
            m.m10, m.m11, m.m12, m.m13,
            m.m20, m.m21, m.m22, m.m23,
            m.m30, m.m31, m.m32, m.m33
        );
    }

    public Mat4 toIdentity() {
        return set(
            1, 0, 0, 0,
            0, 1, 0, 0, 
            0, 0, 1, 0,
            0, 0, 0, 1
        );
    }

    public float[] get(float[] buf) {
        int i = 0;

        buf[i++] = m00;
        buf[i++] = m10;
        buf[i++] = m20;
        buf[i++] = m30;

        buf[i++] = m01;
        buf[i++] = m11;
        buf[i++] = m21;
        buf[i++] = m31;

        buf[i++] = m02;
        buf[i++] = m12;
        buf[i++] = m22;
        buf[i++] = m32;

        buf[i++] = m03;
        buf[i++] = m13;
        buf[i++] = m23;
        buf[i++] = m33;

        return buf;
    }

    public Mat4 transpose() {
        return set(
            m00, m10, m20, m30, 
            m01, m11, m21, m31, 
            m02, m12, m22, m32, 
            m03, m13, m23, m33
        );
    }

    private float fma(float a, float b, float c) {
        return a * b + c;
    }

    public Mat4 invert() {
        float a = m00 * m11 - m01 * m10;
        float b = m00 * m12 - m02 * m10;
        float c = m00 * m13 - m03 * m10;
        float d = m01 * m12 - m02 * m11;
        float e = m01 * m13 - m03 * m11;
        float f = m02 * m13 - m03 * m12;
        float g = m20 * m31 - m21 * m30;
        float h = m20 * m32 - m22 * m30;
        float i = m20 * m33 - m23 * m30;
        float j = m21 * m32 - m22 * m31;
        float k = m21 * m33 - m23 * m31;
        float l = m22 * m33 - m23 * m32;
        float det = a * l - b * k + c * j + d * i - e * h + f * g;
        det = 1.0f / det;
        float nm00 = fma( m11, l, fma(-m12, k,  m13 * j)) * det;
        float nm01 = fma(-m01, l, fma( m02, k, -m03 * j)) * det;
        float nm02 = fma( m31, f, fma(-m32, e,  m33 * d)) * det;
        float nm03 = fma(-m21, f, fma( m22, e, -m23 * d)) * det;
        float nm10 = fma(-m10, l, fma( m12, i, -m13 * h)) * det;
        float nm11 = fma( m00, l, fma(-m02, i,  m03 * h)) * det;
        float nm12 = fma(-m30, f, fma( m32, c, -m33 * b)) * det;
        float nm13 = fma( m20, f, fma(-m22, c,  m23 * b)) * det;
        float nm20 = fma( m10, k, fma(-m11, i,  m13 * g)) * det;
        float nm21 = fma(-m00, k, fma( m01, i, -m03 * g)) * det;
        float nm22 = fma( m30, e, fma(-m31, c,  m33 * a)) * det;
        float nm23 = fma(-m20, e, fma( m21, c, -m23 * a)) * det;
        float nm30 = fma(-m10, j, fma( m11, h, -m12 * g)) * det;
        float nm31 = fma( m00, j, fma(-m01, h,  m02 * g)) * det;
        float nm32 = fma(-m30, d, fma( m31, b, -m32 * a)) * det;
        float nm33 = fma( m20, d, fma(-m21, b,  m22 * a)) * det;

        return set( 
            nm00, nm01, nm02, nm03,
            nm10, nm11, nm12, nm13,
            nm20, nm21, nm22, nm23,
            nm30, nm31, nm32, nm33
        );
    }

    public Mat4 translate(float x, float y, float z) {
        return mul( 
            1, 0, 0, x,
            0, 1, 0, y,
            0, 0, 1, z,
            0, 0, 0, 1
        );
    }

    public Mat4 translate(Vec3 v) {
        return translate(v.x, v.y, v.z);
    }

    public Mat4 scale(float x, float y, float z) {
        return mul( 
            x, 0, 0, 0,
            0, y, 0, 0,
            0, 0, z, 0,
            0, 0, 0, 1
        );
    }

    public Mat4 scale(Vec3 s) {
        return scale(s.x, s.y, s.z);
    }

    public Mat4 rotate(float degrees, float x, float y, float z) {
        float radians = (float)Math.toRadians(degrees);
        float c = (float)Math.cos(radians);
        float s = (float)Math.sin(radians);
        float l = (float)Math.sqrt(x * x + y * y + z * z);

        x /= l;
        y /= l;
        z /= l;
        return mul( 
            x * x * (1 - c) + c, x * y * (1 - c) - z * s, x * z * (1 - c) + y * s, 0,
            y * x * (1 - c) + z * s, y * y * (1 - c) + c, y * z * (1 - c) - x * s, 0,
            x * z * (1 - c) - y * s, y * z * (1 - c) + x * s, z * z * (1 - c) + c, 0,
            0, 0, 0, 1
        );
    }

    public Mat4 rotate(float degrees, Vec3 axis) {
        return rotate(degrees, axis.x, axis.y, axis.z);
    }

    public Mat4 ortho(float left, float right, float bottom, float top, float zNear, float zFar) {
        float sx = 2 / (right - left);
        float sy = 2 / (top - bottom);
        float sz = -2 / (zFar - zNear);
        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(zFar + zNear) / (zFar - zNear);

        return mul( 
            sx, 0, 0, tx,
            0, sy, 0, ty,
            0, 0, sz, tz,
            0, 0, 0, 1
        );
    }

    public Mat4 perspective(float fovDegrees, float aspectRatio, float zNear, float zFar) {
        float radians = (float)Math.toRadians(fovDegrees);
        float f = 1 / (float)Math.tan(radians / 2);
        return mul( 
            f / aspectRatio, 0, 0, 0,
            0, f, 0, 0,
            0, 0, (zFar + zNear) / (zNear - zFar), 2 * zFar * zNear / (zNear - zFar),
            0, 0, -1, 0
        );
    }

    public Mat4 lookAt(float ex, float ey, float ez, float tx, float ty, float tz, float ux, float uy, float uz) {
        float fx = tx - ex;
        float fy = ty - ey;
        float fz = tz - ez;
        float fl = (float)Math.sqrt(fx * fx + fy * fy + fz * fz);
        float ul = (float)Math.sqrt(ux * ux + uy * uy + uz * uz);

        fx /= fl;
        fy /= fl;
        fz /= fl;
        ux /= ul;
        uy /= ul;
        uz /= ul;

        float rx = fy * uz - fz * uy;
        float ry = fz * ux - fx * uz;
        float rz = fx * uy - fy * ux;
        float rl = (float)Math.sqrt(rx * rx + ry * ry + rz * rz);

        rx /= rl;
        ry /= rl;
        rz /= rl;

        ux = ry * fz - rz * fy;
        uy = rz * fx - rx * fz;
        uz = rx * fy - ry * fx;
        ul = (float)Math.sqrt(ux * ux + uy * uy + uz * uz);

        fx = -fx;
        fy = -fy;
        fz = -fz;

        return mul(
            rx, ry, rz, -(ex * rx + ey * ry + ez * rz),
            ux, uy, uz, -(ex * ux + ey * uy + ez * uz),
            fx, fy, fz, -(ex * fx + ey * fy + ez * fz), 
            0, 0, 0, 1
        );
    }

    public Mat4 lookAt(Vec3 eye, Vec3 target, Vec3 up) {
        return lookAt(eye.x, eye.y, eye.z, target.x, target.y, target.z, up.x, up.y, up.z);
    }

    public Vec4 transform(Vec4 v) {
        float tx = m00 * v.x + m01 * v.y + m02 * v.z + m03 * v.w;
        float ty = m10 * v.x + m11 * v.y + m12 * v.z + m13 * v.w;
        float tz = m20 * v.x + m21 * v.y + m22 * v.z + m23 * v.w;
        float tw = m30 * v.x + m31 * v.y + m32 * v.z + m33 * v.w;

        return v.set(tx, ty, tz, tw);
    }

    public Vec3 transform(Vec3 v) {
        float tx = m00 * v.x + m01 * v.y + m02 * v.z + m03;
        float ty = m10 * v.x + m11 * v.y + m12 * v.z + m13;
        float tz = m20 * v.x + m21 * v.y + m22 * v.z + m23;

        return v.set(tx, ty, tz);
    }

    public Vec3 transformNormal(Vec3 v) {
        float tx = m00 * v.x + m01 * v.y + m02 * v.z;
        float ty = m10 * v.x + m11 * v.y + m12 * v.z;
        float tz = m20 * v.x + m21 * v.y + m22 * v.z;

        return v.set(tx, ty, tz);
    }

    public Vec2 transform(Vec2 v) {
        float tx = m00 * v.x + m01 * v.y + m03;
        float ty = m10 * v.x + m11 * v.y + m13;

        return v.set(tx, ty);
    }

    public Vec2 transformNormal(Vec2 v) {
        float tx = m00 * v.x + m01 * v.y;
        float ty = m10 * v.x + m11 * v.y;

        return v.set(tx, ty);
    }

    @Override
    public String toString() {
        return 
        m00 + " " + m01 + " " + m02 + " " + m03 + "\n" +
        m10 + " " + m11 + " " + m12 + " " + m13 + "\n" +
        m20 + " " + m21 + " " + m22 + " " + m23 + "\n" +
        m30 + " " + m31 + " " + m32 + " " + m33 + "\n";
    }
}
