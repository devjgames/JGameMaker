package org.game;

import java.io.File;
import java.util.Vector;

public interface Renderable {

    File getFile();

    AABB getBounds();

    int getTriangleCount();

    Triangle getTriangle(Scene scene, SceneNode node, int i, Triangle triangle);

    void update(Scene scene, SceneNode node) throws Exception;

    int render(Scene scene, SceneNode node, Vector<SceneNode> lights) throws Exception;
    
    Renderable newInstance() throws Exception;
}
