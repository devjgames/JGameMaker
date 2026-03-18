package org.game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;

final class MD2 {
    
    public static final class Header {
        public final int id;
        public final int version;
        public final int skinWidth;
        public final int skinHeight;
        public final int frameSize;
        public final int numSkins;
        public final int numVertices;
        public final int numST;
        public final int numTris;
        public final int numGLCmds;
        public final int numFrames;
        public final int offSkins;
        public final int offST;
        public final int offTris;
        public final int offFrames;
        public final int offGLCmds;
        public final int offEnd;

        public Header(BinReader reader) throws IOException {
            id = reader.readInt();
            version = reader.readInt();
            skinWidth = reader.readInt();
            skinHeight = reader.readInt();
            frameSize = reader.readInt();
            numSkins = reader.readInt();
            numVertices = reader.readInt();
            numST = reader.readInt();
            numTris = reader.readInt();
            numGLCmds = reader.readInt();
            numFrames = reader.readInt();
            offSkins = reader.readInt();
            offST = reader.readInt();
            offTris = reader.readInt();
            offFrames = reader.readInt();
            offGLCmds = reader.readInt();
            offEnd = reader.readInt();
        }
    }

    public static final class TextureCoordinate {
        public final int s;
        public final int t;

        public TextureCoordinate(BinReader reader) throws IOException {
            s = reader.readShort();
            t = reader.readShort();
        }
    }

    public static final class TriangleIndices {
        public final int[] vertex = new int[3];
        public final int[] st = new int[3];

        public TriangleIndices(BinReader reader) throws IOException {
            vertex[0] = reader.readShort();
            vertex[1] = reader.readShort();
            vertex[2] = reader.readShort();
            st[0] = reader.readShort();
            st[1] = reader.readShort();
            st[2] = reader.readShort();
        }
    }

    public static final class TriangleVertex {
        public final int[] v = new int[3];
        public final int n;

        public TriangleVertex(BinReader reader) throws IOException {
            v[0] = reader.readByte();
            v[1] = reader.readByte();
            v[2] = reader.readByte();
            n = reader.readByte();
        }
    }

    public static final class Frame {
        public final Vec3 scale = new Vec3();
        public final Vec3 translate = new Vec3();
        public final String name;
        public final TriangleVertex[] vertices;
        public final AABB bounds;

        public Frame(BinReader reader, Header header) throws IOException {
            scale.x = reader.readFloat();
            scale.y = reader.readFloat();
            scale.z = reader.readFloat();
            translate.x = reader.readFloat();
            translate.y = reader.readFloat();
            translate.z = reader.readFloat();
            name = reader.readString(16);
            vertices = new TriangleVertex[header.numVertices];
            bounds = new AABB();
            for(int i = 0; i != header.numVertices; i++) {
                vertices[i] = new TriangleVertex(reader);
                bounds.add(
                    vertices[i].v[0] * scale.x + translate.x,
                    vertices[i].v[1] * scale.y + translate.y,
                    vertices[i].v[2] * scale.z + translate.z
                );
            }
        }
    }

    public static void createMD2(File objFramesDirectory) throws Exception {
        File md2File = IO.file(AssetManager.getRoot(), objFramesDirectory.getName() + ".md2");
        File kfmFile = IO.file(AssetManager.getRoot(), objFramesDirectory.getName() + ".kfm");
        File texFile = IO.file(objFramesDirectory.getParentFile(), IO.getFilenameWithoutExtension(objFramesDirectory) + ".png");

        if(!texFile.exists()) {
            throw new Exception("missing skin file '" + texFile.getName() + "'");
        } else {
            IO.writeAllBytes(IO.readAllBytes(texFile), IO.file(AssetManager.getRoot(), texFile.getName()));
        }

        File[] files = objFramesDirectory.listFiles();
        Vector<String> names = new Vector<>();

        if(files != null) {
            for(File file : files) {
                if(!file.isDirectory() && IO.getExtension(file).equals(".obj")) {
                    names.add(file.getName());
                } 
            }
        }
        if(names.isEmpty()) {
            throw new Exception("did not find any obj frames");
        }

        names.sort((a, b) -> a.compareTo(b));

        Vector<Vector<Vertex>> frames = new Vector<>();

        for(String name : names) {
            File file = IO.file(objFramesDirectory, name);

            Log.put(1, "processing '" + file + "' ...");

            String[] lines = new String(IO.readAllBytes(file)).split("\\n+");
            Vector<Vec3> vList = new Vector<>();
            Vector<Vec2> tList = new Vector<>();
            Vector<Vec3> nList = new Vector<>();
            Vector<Vertex> frame = new Vector<>();

            for(String line : lines) {
                String tLine = line.trim();
                String[] tokens = tLine.split("\\s+");

                if(tLine.startsWith("v ")) {
                    vList.add(new Vec3(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                } else if(tLine.startsWith("vt ")) {
                    tList.add(new Vec2(Float.parseFloat(tokens[1]), 1 - Float.parseFloat(tokens[2])));
                } else if(tLine.startsWith("vn ")) {
                    nList.add(new Vec3(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                } else if(tLine.startsWith("f ")) {
                    Vector<Vertex> face = new Vector<>();
                    int tris = tokens.length - 3;

                    for(int i = 1; i != tokens.length; i++) {
                        String[] iTokens = tokens[i].split("/");
                        int vI = Integer.parseInt(iTokens[0]) - 1;
                        int tI = Integer.parseInt(iTokens[1]) - 1;
                        int nI = Integer.parseInt(iTokens[2]) - 1;
                        Vertex v = new Vertex();
                        
                        v.position.set(vList.get(vI));
                        v.textureCoordinate.set(tList.get(tI));
                        v.normal.set(nList.get(nI));

                        face.add(v);
                    }
                    for(int i = 0; i != tris; i++) {
                        frame.add(face.get(0).newInstance());
                        frame.add(face.get(i + 1).newInstance());
                        frame.add(face.get(i + 2).newInstance());
                    }
                }
            }

            if(!frames.isEmpty()) {
                if(frames.firstElement().size() != frame.size()) {
                    throw new Exception("invalid frame vertex count");
                }
            }
            frames.add(frame);
        }

        float[][] normals = MD2Normals.cloneNormals();
        Vector<Integer> bytes = new Vector<>();
        BufferedImage tex = ImageIO.read(texFile);
        int w = tex.getWidth();
        int h = tex.getHeight();
        int frameSize = frames.get(0).size() *  4 + 12 + 12 + 16;
        int headerSize = 17 * 4;
        int offSkins = 0;
        int offST = headerSize;
        int offTris = offST + frames.get(0).size() * 4;
        int offFrames = offTris + frames.get(0).size() / 3 * 12;
        int offCmds = 0;
        int offEnd = offFrames + frames.size() * frameSize;

        appendInteger(84412116, bytes); // id
        appendInteger(8, bytes); // version
        appendInteger(w, bytes); // skin w
        appendInteger(h, bytes); // skin h
        appendInteger(frameSize, bytes); // frame size
        appendInteger(0, bytes); // num skins
        appendInteger(frames.get(0).size(), bytes); // num verts
        appendInteger(frames.get(0).size(), bytes); // num st
        appendInteger(frames.get(0).size() / 3, bytes); // num tris
        appendInteger(0, bytes); // num commands
        appendInteger(frames.size(), bytes); // num frames
        appendInteger(offSkins, bytes); // off skins
        appendInteger(offST, bytes); // off st
        appendInteger(offTris, bytes); // off tris 
        appendInteger(offFrames, bytes); // off frames 
        appendInteger(offCmds, bytes); // off cmds
        appendInteger(offEnd, bytes); // off end

        for(Vertex v : frames.get(0)) {
            int s = (int)(v.textureCoordinate.x * w);
            int t = (int)(v.textureCoordinate.y * h);

            appendShort(s, bytes);
            appendShort(t, bytes);
        }
        for(int i = 0, j = 0; i != frames.get(0).size() / 3; i++, j += 3) {
            appendShort(j + 2, bytes);
            appendShort(j + 1, bytes);
            appendShort(j + 0, bytes);
            appendShort(j + 2, bytes);
            appendShort(j + 1, bytes);
            appendShort(j + 0, bytes);
        }
        for(Vector<Vertex> frame : frames) {
            AABB bounds = new AABB();

            for(Vertex v : frame) {
                bounds.add(v.position);
            }

            Vec3 translation = new Vec3(bounds.min);
            Vec3 size = bounds.size(new Vec3());
            Vec3 scale = new Vec3(255, 255, 255).div(size);

            appendFloat(1 / scale.x, bytes);
            appendFloat(1 / scale.y, bytes);
            appendFloat(1 / scale.z, bytes);
            appendFloat(translation.x, bytes);
            appendFloat(translation.y, bytes);
            appendFloat(translation.z, bytes);
            for(int i = 0; i != 16; i++) {
                bytes.add(0);
            }
            for(Vertex v : frame) {
                Vec3 n = v.normal.normalize(new Vec3());
                int j = 0;
                float max = -Float.MAX_VALUE;

                for(int i = 0; i != normals.length; i++) {
                    float nx = normals[i][0];
                    float ny = normals[i][1];
                    float nz = normals[i][2];
                    float d = n.dot(nx, ny, nz);

                    if(d > max) {
                        j = i;
                        max = d;
                    }
                }

                Vec3 p = new Vec3(v.position);

                p.sub(translation);
                p.scale(scale);

                int vx = (int)p.x;
                int vy = (int)p.y;
                int vz = (int)p.z;

                bytes.add(vx);
                bytes.add(vy);
                bytes.add(vz);
                bytes.add(j);
            }
        }

        byte[] b = new byte[bytes.size()];

        for(int i = 0; i != bytes.size(); i++) {
            int j = bytes.get(i);

            b[i] = (byte)(j & 0xFF);
        }
        IO.writeAllBytes(b, md2File);

        IO.writeAllBytes(("mesh " + md2File.getName() + "\ntexture " + texFile.getName()).getBytes(), kfmFile);
    }

    private static void appendInteger(int x, Vector<Integer> bytes) {
        int b1 = x & 0xFF;
        int b2 = (x >> 8) & 0xFF;
        int b3 = (x >> 16) & 0xFF;
        int b4 = (x >> 24) & 0xFF;

        bytes.add(b1);
        bytes.add(b2);
        bytes.add(b3);
        bytes.add(b4);
    }

    private static void appendShort(int x, Vector<Integer> bytes) {
        int b1 = x & 0xFF;
        int b2 = (x >> 8) & 0xFF;

        bytes.add(b1);
        bytes.add(b2);
    }

    private static void appendFloat(float x, Vector<Integer> bytes) {
        appendInteger(Float.floatToIntBits(x), bytes);
    }
}
