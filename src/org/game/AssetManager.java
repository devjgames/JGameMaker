package org.game;

import java.io.File;
import java.util.Hashtable;

public final class AssetManager extends Resource {
    
    private static Hashtable<String, AssetLoader> assetLoaders = new Hashtable<>();
    
    static File root = null;

    static void registerAssetLoader(String extension, AssetLoader assetLoader) {
        assetLoaders.put(extension, assetLoader);
    }

    public static File getRoot() {
        return root;
    }

    public final ResourceManager resources = new ResourceManager();
    
    private Hashtable<String, Object> assets = new Hashtable<>();

    AssetManager() {
        registerAssetLoader(".png", new Texture.Loader());
        registerAssetLoader(".wav", new SoundLoader());
        registerAssetLoader(".kfm", new KeyFrameMesh.KeyFrameMeshLoader());
        registerAssetLoader(".obj", new MeshLoader());
    }

    @SuppressWarnings("unchecked")
    public <T extends Object> T load(File file) throws Exception {
        String key = file.getPath();

        if(!assets.containsKey(key)) {
            Log.put(1, "loading asset '" + key + "' ...");
            assets.put(key, assetLoaders.get(IO.getExtension(file)).load(IO.file(root, key), this));
        }
        return (T)assets.get(key);
    }

    void unload(File file) throws Exception {
        String key = file.getPath();

        if(assets.containsKey(key)) {
            Object asset = assets.get(key);

            if(asset instanceof Resource) {
                ((Resource)asset).destroy();
            }
            assets.remove(key);
        }
    }

    void clear() throws Exception {
        resources.clear();

        for(String key : assets.keySet()) {
            Object asset = assets.get(key);

            if(asset instanceof Resource) {
                ((Resource)asset).destroy();
            }
        }
        assets.clear();
    }

    @Override
    void destroy() throws Exception {
        clear();
        resources.destroy();
        super.destroy();
    }
}
