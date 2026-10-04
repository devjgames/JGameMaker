package org.game;

import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

public final class SceneNodeComponent {
    
    public final Hashtable<String, Object> properties = new Hashtable<>();
    public final Vector<String> propertyNames = new Vector<>();

    private SceneNode node = null;
    private Scene scene = null;
    private ScriptEngine engine;
    private Bindings bindings;
    private final File jsFile;
    private final String type;

    boolean isComplete = false;

    SceneNodeComponent(ScriptEngine engine, File jsFile) {
        this.engine = engine;
        this.bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        this.jsFile = jsFile;
        this.type = IO.getFilenameWithoutExtension(jsFile);

        try {
            Invocable invocable = (Invocable)engine;

            if(functionExists("create")) {
                invocable.invokeFunction("create", this);
            }
        } catch(Exception ex) {
            Log.put(0, ex);
            engine = null;
        }
    }

    public SceneNode node() {
        return node;
    }

    public Scene scene() {
        return scene;
    }

    public File getJSFile() {
        return jsFile;
    }

    public String getType() {
        return type;
    }

    void init() {
        if(engine != null) {
            try {
                Invocable invocable = (Invocable)engine;

                if(functionExists("init")) {
                    invocable.invokeFunction("init", this);
                }
            } catch(Exception ex) {
                Log.put(0, ex);
                engine = null;
            }
        }
    }

    void start() {
        if(engine != null) {
            try {
                Invocable invocable = (Invocable)engine;

                if(functionExists("start")) {
                    invocable.invokeFunction("start", this);
                }
            } catch(Exception ex) {
                Log.put(0, ex);
                engine = null;
            }
        }
    }

    void update() {
        if(engine != null) {
            try {
                Invocable invocable = (Invocable)engine;

                if(functionExists("update")) {
                    invocable.invokeFunction("update", this);
                }
            } catch(Exception ex) {
                Log.put(0, ex);
                engine = null;
            }
        }
    }

    void renderSprites(SpriteRenderer renderer) {
        if(engine != null) {
            try {
                Invocable invocable = (Invocable)engine;

                if(functionExists("renderSprites")) {
                    invocable.invokeFunction("renderSprites", this, renderer);
                }
            } catch(Exception ex) {
                Log.put(0, ex);
                engine = null;
            }
        }
    }

    public void sendMessage(SceneNodeComponent component, String type) {
        if(engine != null) {
            try {
                Invocable invocable = (Invocable)engine;

                if(functionExists("receiveMessage")) {
                    invocable.invokeFunction("receiveMessage", this, component, type);
                }
            } catch(Exception ex) {
                Log.put(0, ex);
                engine = null;
            }
        }
    }

    String loadSceneName() {
        if(engine != null) {
            try {
                Invocable invocable = (Invocable)engine;

                if(functionExists("loadSceneName")) {
                    Object r = invocable.invokeFunction("loadSceneName", this);

                    if(r instanceof String) {
                        return (String)r;
                    }
                }
            } catch(Exception ex) {
                Log.put(0, ex);
                engine = null;
            }
        }
        return null;
    }

    SceneNodeComponent newInstance(Scene scene, SceneNode node) {
        try {
            SceneNodeComponent component = new SceneNodeComponent(engine, jsFile);

            component.node = node;
            component.scene = scene;

            node.addComponent(component);

            for(String key : properties.keySet()) {
                Object value = properties.get(key);
                Class<?> cls = value.getClass();

                if(
                    int.class.isAssignableFrom(cls) ||
                    float.class.isAssignableFrom(cls) ||
                    double.class.isAssignableFrom(cls) ||
                    boolean.class.isAssignableFrom(cls) ||
                    Boolean.class.isAssignableFrom(cls) ||
                    Integer.class.isAssignableFrom(cls) ||
                    Float.class.isAssignableFrom(cls) ||
                    Double.class.isAssignableFrom(cls) ||
                    String.class.isAssignableFrom(cls) || 
                    cls.isEnum()
                ) {
                    component.properties.put(key, value);
                } else if(Vec2.class.isAssignableFrom(cls)) {
                    component.properties.put(key, new Vec2((Vec2)value));
                } else if(Vec3.class.isAssignableFrom(cls)) {
                    component.properties.put(key, new Vec3((Vec3)value));
                } else if(Vec4.class.isAssignableFrom(cls)) {
                    component.properties.put(key, new Vec4((Vec4)value));
                } else if(UIEnum.class.isAssignableFrom(cls)) {
                    UIEnum uiEnum = (UIEnum)component.properties.get(key);

                    uiEnum.setValue(((UIEnum)value).getValue());
                }
            }
            return component;
        } catch(Exception ex) {
            Log.put(0, ex);
        }
        return null;
    }

    private boolean functionExists(String name) {
        if(bindings.containsKey(name)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return type;
    }
}
