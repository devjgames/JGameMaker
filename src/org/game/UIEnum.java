package org.game;

public class UIEnum {
    
    final String[] list;

    private int index = 0;

    public UIEnum(String ... items) throws Exception {
        list = items.clone();

        if(list.length < 2) {
            throw new Exception("enum must have at least 2 items");
        }
    }

    public int getValue() {
        return index;
    }

    public void setValue(int index) throws Exception {
        if(index >= 0 && index < list.length) {
            this.index = index;
        } else {
            throw new Exception("invalid enum value");
        }
    }
}
