package org.game;

public class Frustum {

    public static void normalize(Vec4 plane) {
        float length = (float)Math.sqrt(plane.x * plane.x + plane.y * plane.y + plane.z * plane.z);

        plane.div(length);
    }

    private final Vec4[] planes = new Vec4[] {
        new Vec4(),
        new Vec4(),
        new Vec4(),
        new Vec4(),
        new Vec4(),
        new Vec4()
    };
    private final Mat4 matrix = new Mat4();
    private final AABB bounds = new AABB();

    public void calcPlanes(Mat4 projection, Mat4 view) {
        matrix.set(projection).mul(view);
        matrix.transpose();

        planes[0].set(
            matrix.m03 + matrix.m00,
            matrix.m13 + matrix.m10,
            matrix.m23 + matrix.m20,
            matrix.m33 + matrix.m30
        );

        planes[1].set(
            matrix.m03 - matrix.m00,
            matrix.m13 - matrix.m10,
            matrix.m23 - matrix.m20,
            matrix.m33 - matrix.m30
        );

        planes[2].set(
            matrix.m03 + matrix.m01,
            matrix.m13 + matrix.m11,
            matrix.m23 + matrix.m21,
            matrix.m33 + matrix.m31
        );

        planes[3].set(
            matrix.m03 - matrix.m01,
            matrix.m13 - matrix.m11,
            matrix.m23 - matrix.m21,
            matrix.m33 - matrix.m31
        );

        planes[4].set(
            matrix.m03 + matrix.m02,
            matrix.m13 + matrix.m12,
            matrix.m23 + matrix.m22,
            matrix.m33 + matrix.m32
        );

        planes[5].set(
            matrix.m03 - matrix.m02,
            matrix.m13 - matrix.m12,
            matrix.m23 - matrix.m22,
            matrix.m33 - matrix.m32
        );

        for(Vec4 plane : planes) {
            normalize(plane);
        }
    }

    public boolean contains(AABB b) {
        if(b.isEmpty()) {
            return false;
        }
        for(Vec4 plane : planes) {
            float minD = plane.w;
            float maxD = plane.w;

            minD += (plane.x < 0) ? b.max.x * plane.x : b.min.x * plane.x;
            minD += (plane.y < 0) ? b.max.y * plane.y : b.min.y * plane.y;
            minD += (plane.z < 0) ? b.max.z * plane.z : b.min.z * plane.z;

            maxD += (plane.x > 0) ? b.max.x * plane.x : b.min.x * plane.x;
            maxD += (plane.y > 0) ? b.max.y * plane.y : b.min.y * plane.y;
            maxD += (plane.z > 0) ? b.max.z * plane.z : b.min.z * plane.z;

            if(minD < 0 && maxD < 0) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(Vec3 center, float radius) {
        return contains(
            bounds.set(
                center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius
            )
        );
    }
}