package org.game;

public class UI {
    
    public static void rebuildTree() {
        SceneNode node = UIGameEditor.getInstance().getSelection();

        UIGameEditor.getInstance().getSceneTree().populate();

        if(node.getRoot() == UIGameEditor.getInstance().getScene().root) {
            UIGameEditor.getInstance().getSceneTree().setSelection(node);
        }
    }
}
