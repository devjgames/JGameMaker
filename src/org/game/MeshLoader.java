package org.game;

import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

class MeshLoader implements AssetLoader {

    @Override
    public Object load(File file, AssetManager assets) throws Exception {
        String[] lines = new String(IO.readAllBytes(file)).split("\\n+");
        Vector<Vec3> vList = new Vector<>();
        Vector<Vec2> tList = new Vector<>();
        Vector<Vec3> nList = new Vector<>();

            Hashtable<String, Mesh.MeshPart> keyedMeshParts = new Hashtable<>();
            Hashtable<String, String> materials = new Hashtable<>();
            HashSet<String> textures = new HashSet<>();
            String material = "";

            for (String line : lines) {
                String tLine = line.trim();
                String[] tokens = tLine.split("\\s+");
                if(tLine.startsWith("mtllib ")) {
                    loadMaterials(new File(file.getParent(), tLine.substring(6).trim()), materials);
                } else if(tLine.startsWith("usemtl ")) {
                    material = materials.get(tLine.substring(6).trim());
                    if(material == null) {
                        material = "";
                    }
                    if(!keyedMeshParts.containsKey(material)) {
                        textures.add(material);
                        keyedMeshParts.put(material, new Mesh.MeshPart());
                    }
                } else if (tLine.startsWith("v ")) {
                    vList.add(new Vec3(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                } else if (tLine.startsWith("vt ")) {
                    tList.add(new Vec2(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));;
                    tList.lastElement().y = 1 - tList.lastElement().y;
                } else if (tLine.startsWith("vn ")) {
                    nList.add(new Vec3(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])));
                } else if (tLine.startsWith("f ")) {
                    if(!keyedMeshParts.containsKey(material)) {
                        keyedMeshParts.put(material, new Mesh.MeshPart());
                    }
                    Mesh.MeshPart meshPart = keyedMeshParts.get(material);
                    int bV = meshPart.vertices.size();
                    int[] polygon = new int[tokens.length - 1];
                    
                    for (int i = 1; i != tokens.length; i++) {
                        String[] iTokens = tokens[i].split("[/]+");
                        int vI = Integer.parseInt(iTokens[0]) - 1;
                        int tI = Integer.parseInt(iTokens[1]) - 1;
                        int nI = Integer.parseInt(iTokens[2]) - 1;
                        Vertex vertex = new Vertex();

                        vertex.position.set(vList.get(vI));
                        vertex.textureCoordinate.set(tList.get(tI));
                        vertex.normal.set(nList.get(nI));
                        meshPart.vertices.add(vertex);

                        polygon[i - 1] = bV + (i - 1);
                    }
                    meshPart.addPolygon(polygon);
                }
            }
            Enumeration<String> keys = keyedMeshParts.keys();
            Vector<String> sortedKeys = new Vector<>();

            while(keys.hasMoreElements()) {
                sortedKeys.add(keys.nextElement());
            }
            sortedKeys.sort((a, b) -> a.compareTo(b));

            for(String key : sortedKeys) {
                Mesh.MeshPart meshPart = keyedMeshParts.get(key);

                if(meshPart.indices.size() != 0) {
                    if(textures.contains(key)) {
                        if(!key.isEmpty()) {
                            meshPart.texture = assets.load(IO.file(key));
                            meshPart.loadDecal();
                        }
                    }
                    meshPart.calcBounds();
                }
            }

            Mesh mesh = new Mesh(file);

            for(String key : sortedKeys) {
                Mesh.MeshPart meshPart = keyedMeshParts.get(key);

                if(meshPart.indices.size() != 0) {
                    mesh.parts.add(meshPart);
                }
            }
            mesh.calcBounds();

            if(!mesh.isValid()) {
                throw new Exception("invalid mesh");
            }

            return mesh;
    }

    private void loadMaterials(File file, Hashtable<String, String> materials) throws Exception {
        String[] lines = new String(IO.readAllBytes(file)).split("\\n+");
        String name = null;

        for(String line : lines) {
            String tLine = line.trim();
            if(tLine.startsWith("newmtl ")) {
                name = tLine.substring(6).trim();
            } else if(tLine.startsWith("map_Kd ")) {
                materials.put(name, tLine.substring(6).trim());
            }
        }
    }
}
