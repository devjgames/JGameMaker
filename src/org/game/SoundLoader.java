package org.game;

import java.io.File;

class SoundLoader implements AssetLoader {

    @Override
    public Object load(File file, AssetManager assets) throws Exception {
        return new Sound(file);
    }
    
}
