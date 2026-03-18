package org.game;

public class UIButton {
   
    public static interface Click {
    
        void onClick(SceneNodeComponent component);
    }

    final String text;
    private final Click click;

    public UIButton(String text, Click click) {
        this.text = text;
        this.click = click;
    }

    void clicked(SceneNodeComponent component) {
        click.onClick(component);
    }
}
