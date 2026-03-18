package org.game;

import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

class UITopBar extends JPanel {

    public static final int ZOOM = 0;
    public static final int SELECT = 1;
    public static final int ROTATE = 2;
    public static final int PANXZ = 3;
    public static final int PANY = 4;
    public static final int MOVXZ = 5;
    public static final int MOVY = 6;
    public static final int ROTX = 7;
    public static final int ROTY = 8;
    public static final int ROTZ = 9;
    public static final int SCALE = 10;
    public static final int PAINT = 11;

    private Hashtable<String, JToggleButton> toggleButtons = new Hashtable<>();
    private Hashtable<String, JButton> buttons = new Hashtable<>();
    private String[] captions = new String [] {
        "Zoom", "Select", "Rotate", "Pan XZ", "Pan Y", "Move XZ", "Move Y", "Rot X", "Rot Y", "Rot Z", "Scale", "Paint"
    };
    private int mode = 0;
    private ButtonGroup group = new ButtonGroup();

    public UITopBar() {
        super(new FlowLayout(FlowLayout.LEFT, 5, 5));

        for(String caption : captions) {
            toggleButtons.put(caption, new JToggleButton(new AbstractAction(caption) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JToggleButton button = (JToggleButton)e.getSource();

                    for(int i = 0; i != captions.length; i++) {
                        if(button.getText().equals(captions[i])) {
                            if(i != mode && i == PAINT) {
                                UIGameEditor.getInstance().startPainting();
                            }
                            mode = i;
                            if(mode != PAINT) {
                                UIGameEditor.getInstance().stopPainting();
                            }
                            break;
                        }
                    }
                }
            }));
            if(caption.equals("Paint")) {
                toggleButtons.get(caption).setToolTipText("left click mark, right click rotate 45, shift left click delete");
            }
            group.add(toggleButtons.get(caption));
            add(toggleButtons.get(caption));
        }

        toggleButtons.get("Zoom").setSelected(true);

        buttons.put("Next Brush", new JButton(new AbstractAction("Next Brush") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor.getInstance().nextBrush();
            }
        }));
        add(buttons.get("Next Brush"));

        buttons.put("Shot", new JButton(new AbstractAction("Shot") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor.getInstance().takeShot();
            }
        }));
        add(buttons.get("Shot"));

        buttons.put("Java Docs", new JButton(new AbstractAction("Java Docs") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().open(IO.file("Docs/index.html"));
                } catch(Exception ex) {
                    Log.put(0, ex);
                }
            }
        }));
        add(buttons.get("Java Docs"));
    }

    public int getMode() {
        return mode;
    }

    public void enableUI() {
        boolean enabled = UIGameEditor.getInstance().getScene() != null;

        for(String caption : captions) {
            toggleButtons.get(caption).setEnabled(enabled && !UIGameEditor.getInstance().isPlaying());
        }
        buttons.get("Shot").setEnabled(enabled);
        buttons.get("Next Brush").setEnabled(enabled && !UIGameEditor.getInstance().isPlaying());
    }
}
