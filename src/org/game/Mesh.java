package org.game;

import java.io.File;
import java.util.Vector;

final class Mesh implements Renderable {

    static final class MeshPart {

        Texture texture = null;
        Texture decal = null;

        final Vector<Vertex> vertices;
        final Vector<Integer> indices;
        final Vector<int[]> polygons;
        final AABB bounds = new AABB();
        
        private final Vertex vertex = new Vertex();

        public MeshPart() {
            vertices = new Vector<>();
            indices = new Vector<>();
            polygons = new Vector<>();
        }

        public MeshPart(MeshPart part) {
            if(part.texture != null) {
                try {
                    texture = part.texture.newInstance();
                } catch(Exception ex) {
                    Log.put(0, ex);
                }
            }
            if(part.decal != null) {
                try {
                    decal = part.decal.newInstance();
                } catch(Exception ex) {
                    Log.put(0, ex);
                }
            }
            vertices = part.vertices;
            indices = part.indices;
            polygons = part.polygons;
            bounds.set(part.bounds);
        }

        public void calcBounds() {
            bounds.clear();
            for(Vertex v : vertices) {
                bounds.add(v.position);
            }
        }

        public void addVertex(float x, float y, float z, float s, float t, float nx, float ny, float nz) throws Exception {
            vertex.position.set(x, y, z);
            vertex.textureCoordinate.set(s, t);
            vertex.normal.set(nx, ny, nz);
            if(vertex.normal.length() > 0.0000001) {
                vertex.normal.normalize();
            }
            vertices.add(vertex.newInstance());
        }

        public void addPolygon(int ... indices) {
            int tris = indices.length - 2;

            for(int i = 0; i != tris; i++) {
                this.indices.add(indices[0]);
                this.indices.add(indices[i + 1]);
                this.indices.add(indices[i + 2]);
            }
            polygons.add(indices.clone());
        }

        public void loadDecal() {
            if(texture != null) {
                File file = IO.file(AssetManager.getRoot(), IO.getFilenameWithoutExtension(texture.file) + "_DECAL.png");

                if(file.exists()) {
                    try {
                        decal = Game.getInstance().getAssets().load(IO.file(file.getName()));
                    } catch(Exception ex) {
                        Log.put(0, ex);
                    }
                }
            }
        }
    }

    final Vector<MeshPart> parts = new Vector<>();

    private File file;
    private final AABB bounds = new AABB();

    public Mesh(File file) {
        this.file = IO.file(file.getName());
    }

    public Mesh(Mesh mesh) {
        file = mesh.file;
        bounds.set(mesh.bounds);

        for(MeshPart part : mesh.parts) {
            parts.add(new MeshPart(part));
        }
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public AABB getBounds() {
        return bounds;
    }

    @Override
    public int getTriangleCount() {
        int count = 0;

        for(MeshPart part : parts) {
            count += part.indices.size() / 3;
        }
        return count;
    }

    @Override
    public Triangle getTriangle(Scene scene, SceneNode node, int i, Triangle triangle) {
        int count = 0;

        i *= 3;
        for(MeshPart part : parts) {
            int j = i - count;
            count += part.indices.size();
            if(i < count) {
                i = j;
                triangle.p1.set(part.vertices.get(part.indices.get(i + 0)).position);
                triangle.p2.set(part.vertices.get(part.indices.get(i + 1)).position);
                triangle.p3.set(part.vertices.get(part.indices.get(i + 2)).position);
                return triangle.calcPlane();
            }
        }
        return triangle;
    }

    @Override
    public void update(Scene scene, SceneNode node) throws Exception {
    }

    @Override
    public int render(Scene scene, SceneNode node, Vector<SceneNode> lights) throws Exception {
        LightRenderer renderer = Game.getInstance().getRenderer(LightRenderer.class);

        for(MeshPart part : parts) {
            renderer.begin(scene, node, lights, part.texture, part.decal);
            renderer.push(part.vertices, part.indices, part.indices.size());
            renderer.end();
        }
        return getTriangleCount();
    }

    void calcBounds() {
        bounds.clear();
        for(MeshPart part : parts) {
            bounds.add(part.bounds);
        }
    }

    boolean isValid() {
        for(MeshPart part : parts) {
            if(!(part.indices.size() != 0 && part.indices.size() / 3 * 3 == part.indices.size() && part.vertices.size() > 0)) {
                return false;
            }
            for(int i : part.indices) {
                if(i < 0 || i >= part.vertices.size()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Renderable newInstance() throws Exception {
        return new Mesh(this);
    }
}
