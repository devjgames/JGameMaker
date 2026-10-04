package org.game;

import java.io.File;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

final class SceneNodeComponentFactory {

    private static final Hashtable<String, SceneNodeComponent> factories = new Hashtable<>();
    private static final Vector<String> names = new Vector<>();

    public static void compile() {
        
        factories.clear();
        names.clear();

        if(AssetManager.getRoot() != null) {
            File[] files = AssetManager.getRoot().listFiles();

            if(files != null) {
                for(File file : files) {
                    if(file.isFile() && IO.getExtension(file).equals(".js")) {
                        try {
                            Log.put(1, "compiling -> '" + file.getName() + "' ...");

                            ScriptEngineManager manager = new ScriptEngineManager();
                            ScriptEngine engine = manager.getEngineByExtension("js");

                            engine.put(ScriptEngine.FILENAME, file.getName());
                            engine.eval(new String(IO.readAllBytes(file)));

                            SceneNodeComponent component = new SceneNodeComponent(engine, file);

                            factories.put(component.getType(), component);
                            names.add(component.getType());
                        } catch(Exception ex) {
                            Log.put(0, ex);
                        }
                    }
                }
                Collections.sort(names);
            }
        }
    }

    public static Hashtable<String, SceneNodeComponent> getFactories() {
        return factories;
    }

    public static Vector<String> getNames() {
        return names;
    }
}