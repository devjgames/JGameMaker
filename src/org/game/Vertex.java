package org.game;

final class Vertex {
    
    public final Vec3 position = new Vec3();
    public final Vec2 textureCoordinate = new Vec2();
    public final Vec3 normal = new Vec3();
    public final Vec4 color = new Vec4(1, 1, 1, 1);

    public Vertex newInstance() throws Exception {
        Vertex v = new Vertex();

        Utils.copy(this, v);

        return v;
    }
}
