package org.game;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.game.MD2.Frame;
import org.game.MD2.Header;
import org.game.MD2.TextureCoordinate;
import org.game.MD2.TriangleIndices;
import org.game.MD2.TriangleVertex;

public final class KeyFrameMesh implements Renderable {

    static class KeyFrameMeshLoader implements AssetLoader {

        @Override
        public Object load(File file, AssetManager assets) throws Exception {
            String[] lines = new String(IO.readAllBytes(file)).split("\\n+");
            File meshFile = null;
            File directory = file.getParentFile();
            File textureFile = null;

            for(String line : lines) {
                String tLine = line.trim();

                if(tLine.startsWith("texture ")) {
                    textureFile = IO.file(tLine.substring(7).trim());
                } else if(tLine.startsWith("mesh ")) {
                    meshFile = IO.file(directory, tLine.substring(4).trim());
                }
            }

            KeyFrameMesh mesh = new KeyFrameMesh(meshFile, file, textureFile);

            return mesh;
        }
        
    }

    private Texture texture = null;
    private Texture decal = null;
    private final File file;
    private final AABB bounds = new AABB();
    private final AABB tBounds = new AABB();
    private final Header header;
    private final TextureCoordinate[] textureCoordinates;
    private final TriangleIndices[] triangles;
    private final Frame[] frames;
    private int start = 0;
    private int end = 0;
    private int speed = 0;
    private boolean looping = false;
    private int frame = 0;
    private float amount = 0;
    private boolean done = true;
    private final Vertex vertex = new Vertex();
    private final float[][] normals;

    KeyFrameMesh(File file, File kfmFile, File textureFile) throws IOException {
        BinReader reader = new BinReader(IO.readAllBytes(file));

        this.file = IO.file(kfmFile.getName());

        File decalFile = IO.file(AssetManager.getRoot(), IO.getFilenameWithoutExtension(textureFile) + "_DECAL.png");

        if(decalFile.exists()) {
            try {
                decal = Game.getInstance().getAssets().load(IO.file(decalFile.getName()));
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        }

        if(textureFile != null) {
            try {
                texture = Game.getInstance().getAssets().load(textureFile);
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        }

        header = new Header(reader);
        reader.setPosition(header.offST);
        textureCoordinates = new TextureCoordinate[header.numST];
        for(int i = 0; i != header.numST; i++) {
            textureCoordinates[i] = new TextureCoordinate(reader);
        }
        reader.setPosition(header.offTris);
        triangles = new TriangleIndices[header.numTris];
        for(int i = 0; i != header. numTris; i++) {
            triangles[i] = new TriangleIndices(reader);
        }
        frames = new Frame[header.numFrames];
        for(int i = 0; i != header.numFrames; i++) {
            reader.setPosition(header.offFrames + header.frameSize * i);
            frames[i] = new Frame(reader, header);
        }

        reset();

        bounds.set(frames[0].bounds);

        normals = MD2Normals.cloneNormals();
    }

    KeyFrameMesh(KeyFrameMesh mesh) {
        this.file = mesh.file;
    
        header = mesh.header;
        textureCoordinates = mesh.textureCoordinates;
        triangles = mesh.triangles;
        frames = mesh.frames;

        if(mesh.texture != null) {
            try {
                texture = mesh.texture.newInstance();
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        }

        if(mesh.decal != null) {
            try {
                decal = mesh.decal.newInstance();
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        }
        reset();

        bounds.set(frames[0].bounds);

        normals = mesh.normals;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isLooping() {
        return looping;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getSpeed() {
        return speed;
    }

    public int getFrame() {
        return frame;
    }

    public void reset() {
        frame = start;
        amount = 0;
        done = start == end;
        bounds.set(frames[0].bounds);
    }

    public boolean isSequence(int start, int end) {
        return start == this.start && end == this.end;
    }

    public void setSequence(int start, int end, int speed, boolean looping) {
        if(start >= 0 && start < frames.length && end >= 0 && end < frames.length && start <= end) {
            if(start != this.start || end != this.end || speed != this.speed || looping != this.looping) {
                this.start = start;
                this.end = end;
                this.speed = speed;
                this.looping = looping;
                reset();
            }
        }
    }

    public int getFrameCount() {
        return frames.length;
    }

    public AABB getFrameBounds(int i) {
        return tBounds.set(frames[i].bounds);
    }

    public String getFrameName(int i) {
        return frames[i].name;
    }

    Frame[] getFrames() {
        return frames;
    }

    Header getHeader() {
        return header;
    }

    TextureCoordinate[] getTextureCoordinates() {
        return textureCoordinates;
    }

    TriangleIndices[] getTriangles() {
        return triangles;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public AABB getBounds() {
        return tBounds.set(bounds);
    }

    @Override
    public int getTriangleCount() {
        return header.numTris;
    }

    @Override
    public Triangle getTriangle(Scene scene, SceneNode node, int i, Triangle triangle) {
        int f1 = frame;
        int f2 = frame + 1;

        if(f1 == end) {
            f2 = start;
        }

        TriangleVertex f1v1 = frames[f1].vertices[triangles[i].vertex[0]];
        TriangleVertex f1v2 = frames[f1].vertices[triangles[i].vertex[1]];
        TriangleVertex f1v3 = frames[f1].vertices[triangles[i].vertex[2]];
        TriangleVertex f2v1 = frames[f2].vertices[triangles[i].vertex[0]];
        TriangleVertex f2v2 = frames[f2].vertices[triangles[i].vertex[1]];
        TriangleVertex f2v3 = frames[f2].vertices[triangles[i].vertex[2]];

        float f1x1 = f1v1.v[0] * frames[f1].scale.x + frames[f1].translate.x;
        float f1y1 = f1v1.v[1] * frames[f1].scale.y + frames[f1].translate.y;
        float f1z1 = f1v1.v[2] * frames[f1].scale.z + frames[f1].translate.z;
        float f1x2 = f1v2.v[0] * frames[f1].scale.x + frames[f1].translate.x;
        float f1y2 = f1v2.v[1] * frames[f1].scale.y + frames[f1].translate.y;
        float f1z2 = f1v2.v[2] * frames[f1].scale.z + frames[f1].translate.z;
        float f1x3 = f1v3.v[0] * frames[f1].scale.x + frames[f1].translate.x;
        float f1y3 = f1v3.v[1] * frames[f1].scale.y + frames[f1].translate.y;
        float f1z3 = f1v3.v[2] * frames[f1].scale.z + frames[f1].translate.z;
        float f2x1 = f2v1.v[0] * frames[f2].scale.x + frames[f2].translate.x;
        float f2y1 = f2v1.v[1] * frames[f2].scale.y + frames[f2].translate.y;
        float f2z1 = f2v1.v[2] * frames[f2].scale.z + frames[f2].translate.z;
        float f2x2 = f2v2.v[0] * frames[f2].scale.x + frames[f2].translate.x;
        float f2y2 = f2v2.v[1] * frames[f2].scale.y + frames[f2].translate.y;
        float f2z2 = f2v2.v[2] * frames[f2].scale.z + frames[f2].translate.z;
        float f2x3 = f2v3.v[0] * frames[f2].scale.x + frames[f2].translate.x;
        float f2y3 = f2v3.v[1] * frames[f2].scale.y + frames[f2].translate.y;
        float f2z3 = f2v3.v[2] * frames[f2].scale.z + frames[f2].translate.z;

        triangle.p1.x = f1x1 + amount * (f2x1 - f1x1);
        triangle.p1.y = f1y1 + amount * (f2y1 - f1y1);
        triangle.p1.z = f1z1 + amount * (f2z1 - f1z1);

        triangle.p2.x = f1x2 + amount * (f2x2 - f1x2);
        triangle.p2.y = f1y2 + amount * (f2y2 - f1y2);
        triangle.p2.z = f1z2 + amount * (f2z2 - f1z2);

        triangle.p3.x = f1x3 + amount * (f2x3 - f1x3);
        triangle.p3.y = f1y3 + amount * (f2y3 - f1y3);
        triangle.p3.z = f1z3 + amount * (f2z3 - f1z3);

        return triangle.calcPlane();
    }

    @Override
    public void update(Scene scene, SceneNode node) throws Exception {
        if(done) {
            return;
        }

        amount += speed * Game.getInstance().elapsedTime();
        if(amount >= 1) {
            if(looping) {
                if(frame == end) {
                    frame = start;
                } else {
                    frame++;
                }
                amount = 0;
            } else if(frame == end - 1) {
                amount = 1;
                done = true;
            } else {
                frame++;
                amount = 0;
            }
        }

        int f1 = frame;
        int f2 = frame + 1;

        if(f1 == end) {
            f2 = start;
        }

        AABB b1 = frames[f1].bounds;
        AABB b2 = frames[f2].bounds;

        b1.min.lerp(b2.min, amount, bounds.min);
        b1.max.lerp(b2.max, amount, bounds.max);
    }

    @Override
    public int render(Scene scene, SceneNode node, Vector<SceneNode> lights) throws Exception {
        LightRenderer renderer = Game.getInstance().getRenderer(LightRenderer.class);
        int i1 = frame;
        int i2 = frame + 1;

        if(i1 == end) {
            i2 = start;
        }

        Frame f1 = frames[i1];
        Frame f2 = frames[i2];

        renderer.begin(scene, node, lights, texture, decal);

        for(int i = 0; i != header.numTris; i++) {
            for(int j = 2; j != -1; j--) {
                TriangleVertex v1 = f1.vertices[triangles[i].vertex[j]];
                TriangleVertex v2 = f2.vertices[triangles[i].vertex[j]];
                TextureCoordinate coord = textureCoordinates[triangles[i].st[j]];
                float x1 = v1.v[0] * f1.scale.x + f1.translate.x;
                float y1 = v1.v[1] * f1.scale.y + f1.translate.y;
                float z1 = v1.v[2] * f1.scale.z + f1.translate.z;
                float x2 = v2.v[0] * f2.scale.x + f2.translate.x;
                float y2 = v2.v[1] * f2.scale.y + f2.translate.y;
                float z2 = v2.v[2] * f2.scale.z + f2.translate.z;

                vertex.position.x = x1 + amount * (x2 - x1);
                vertex.position.y = y1 + amount * (y2 - y1);
                vertex.position.z = z1 + amount * (z2 - z1);
                vertex.textureCoordinate.x = coord.s / (float)header.skinWidth;
                vertex.textureCoordinate.y = coord.t / (float)header.skinHeight;
                vertex.normal.x = normals[v1.n][0] + amount * (normals[v2.n][0] - normals[v1.n][0]);
                vertex.normal.y = normals[v1.n][1] + amount * (normals[v2.n][1] - normals[v1.n][1]);
                vertex.normal.z = normals[v1.n][2] + amount * (normals[v2.n][2] - normals[v1.n][2]);

                renderer.push(vertex);
            }
        }
        renderer.end();

        return getTriangleCount();
    }

    @Override
    public Renderable newInstance() throws Exception {
        return new KeyFrameMesh(this);
    }
    
}
