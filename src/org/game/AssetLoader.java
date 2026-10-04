package org.game;

import java.io.File;

interface AssetLoader {

    Object load(File file, AssetManager assets) throws Exception;
    
}
