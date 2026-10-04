package org.game;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

final class SceneSerializer {

    static Scene deserialize(boolean inDesign, File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document =  builder.parse(file);

        SceneNodeComponentFactory.compile();

        Scene scene = (Scene)load(new Scene(file, inDesign), document.getDocumentElement());

        Game.getInstance().resetTimer();

        return scene;
    }

    private static Object load(Scene scene, Element element) throws Exception {
        NodeList nodes = element.getChildNodes();
        Object r = null;

        if(element.getTagName().equals("scene")) {
            load((Object)scene, element);

            for(int i = 0; i != nodes.getLength(); i++) {
                org.w3c.dom.Node xmlNode = nodes.item(i);

                if(xmlNode instanceof Element) {
                    Element element2 = (Element)xmlNode;

                    scene.root.addChild((SceneNode)load(scene, element2));
                }
            }
            r = scene;
        } else {
            SceneNode node = new SceneNode();

            load(node, element);

            if(element.hasAttribute("renderable")) {
                try {
                    Renderable renderable = Game.getInstance().getAssets().load(IO.file(element.getAttribute("renderable")));

                    node.renderable = renderable.newInstance();
                } catch(Exception ex) {
                    Log.put(0, ex);
                } 
            }
            for(int i = 0; i != nodes.getLength(); i++) {
                org.w3c.dom.Node xmlNode = nodes.item(i);

                if(xmlNode instanceof Element) {
                    Element element2 = (Element)xmlNode;

                    if(element2.getTagName().equals("node")) {
                        node.addChild((SceneNode)load(scene, element2));
                    } else {
                        for(String name : SceneNodeComponentFactory.getFactories().keySet()) {
                            if(name.equals(element2.getTagName())) {
                                try {
                                    SceneNodeComponent component = SceneNodeComponentFactory.getFactories().get(name).newInstance(scene, node);
                                    Vector<String> keys = new Vector<>(component.properties.keySet());
                                    
                                    for(String key : keys) {
                                        if(element2.hasAttribute(key)) {
                                            try {
                                                Object obj = Utils.parse(component.properties.get(key), element2.getAttribute(key));

                                                if(obj != null) {
                                                    component.properties.put(key, obj);
                                                }
                                            } catch(Exception ex) {
                                                Log.put(0, ex);
                                            }
                                        }
                                    }
                                    break;
                                } catch(Exception ex) {
                                    Log.put(0, ex);
                                }
                            }
                        }
                    }
                }
            }
            r = node;
        }
        return r;
    }

    private static void load(Object o, Element element) throws Exception {
        Field[] fields = o.getClass().getFields();

        for(Field field : fields) {
            String name = field.getName();

            if(element.hasAttribute(name)) {
                try {
                    Utils.parse(o, name, element.getAttribute(name));
                } catch(Exception ex) {
                    Log.put(0, ex);
                }
            }
        }
    }
    
    static void serialize(Scene scene, File file) throws Exception {
        StringBuilder b = new StringBuilder(10000);

        b.append("<scene");
        append(scene, false, b);
        for(SceneNode node : scene.root) {
            append(node, "\t", b);
        }
        b.append("</scene>\n");

        IO.writeAllBytes(b.toString().getBytes(), file);
    }

    private static void append(Object o, boolean empty, StringBuilder b) throws Exception {
        Class<? extends Object> cls = o.getClass();
        Field[] fields = cls.getFields();

        if(SceneNodeComponent.class.isAssignableFrom(cls)) {
            SceneNodeComponent component = (SceneNodeComponent)o;

            for(String key : component.properties.keySet()) {
                boolean valid = !key.isEmpty();

                if(valid) {
                    for(int i = 0; i != key.length(); i++) {
                        char c = key.charAt(i);

                        if(!(Character.isLetter(c) || Character.isDigit(c) || c == '_')) {
                            valid = false;
                            break;
                        }
                    }
                }

                if(valid) {
                    String s = Utils.string(component.properties.get(key));
                
                    if(s != null) {
                        b.append(" " + key + "=\"" + fix(s) + "\"");
                    }
                }
            }
        } else {
            for(Field field : fields) {
                String s = Utils.string(o, field.getName());

                if(s != null) {
                    b.append(" " + field.getName() + "=\"" + fix(s) + "\"");
                }
            }
        }
        if(empty) {
            b.append("/>\n");
        } else {
            b.append(">\n");
        }
    }

    private static String fix(String value) {
        value = value.replace("&", "&amp;");
        value = value.replace("\"", "&quot;");
        value = value.replace("'", "&apos;");
        value = value.replace("<", "&lt;");
        value = value.replace(">", "&gt;");
        value = value.replace("\n", "&#10;");
        value = value.replace("\t", "&#09;");
        return value;
    }

    private static void append(SceneNode node, String indent, StringBuilder b) throws Exception {
        boolean empty = node.getChildCount() == 0 && node.getComponentCount() == 0;

        b.append(indent + "<node");
        if(node.renderable != null) {
            File file = node.renderable.getFile();

            if(file != null) {
                b.append(" renderable=\"" + file.getName() + "\"");
            }
        }
        append(node, empty, b);
        if(!empty) {
            for(int i = 0; i != node.getComponentCount(); i++) {
                SceneNodeComponent component = node.getComponent(i);

                b.append(indent + "\t<" + component.toString());
                append(component, true, b);
            }
            for(SceneNode child : node) {
                append(child, indent + "\t", b);
            }
            b.append(indent + "</node>\n");
        }
    }
}
