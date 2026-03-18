package org.game;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

class UIEditor extends JPanel {

    private JPanel boxPanel;

    public UIEditor() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        boxPanel = new JPanel();

        BoxLayout boxLayout = new BoxLayout(boxPanel, BoxLayout.Y_AXIS);

        boxPanel.setLayout(boxLayout);
        add(boxPanel);
    }

    public void build(Object obj) {
        try {
            boxPanel.removeAll();

            if(obj == null) {
                getParent().validate();
                return;
            }

            Field[] fields = obj.getClass().getFields();

            for(Field field : fields) {
                new UIFieldEditor().add(obj, field, boxPanel);
            }

            Image delete = load("/org/game/resources/delete.png");
            Image add = load("/org/game/resources/add.png");

            if(obj instanceof SceneNode) {
                SceneNode node = (SceneNode)obj;

                for(int i = 0; i != node.getComponentCount(); i++) {
                    SceneNodeComponent component = node.getComponent(i);
                    JLabel label = new JLabel(component.getType());
                    JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
                    Vector<String> keys = new Vector<>(component.properties.keySet());
                    final int index = i;

                    if(component.propertyNames.isEmpty()) {
                        Collections.sort(keys);
                    } else {
                        keys = component.propertyNames;
                    }

                    label.setFont(new Font(label.getFont().getFamily(), Font.BOLD, 16));
                    flowPanel.add(label);
                    boxPanel.add(flowPanel);

                    for(String name : keys) {
                        new UIPropertyEditor().add(component, name, boxPanel);
                    }

                    JButton button = new JButton(new AbstractAction("", new ImageIcon(delete)) {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            node.removeComponent(node.getComponent(index));
                            build(obj);
                        };
                    });

                    flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
                    flowPanel.add(button);

                    boxPanel.add(flowPanel);
                }

                JComboBox<SceneNodeComponent> comboBox = new JComboBox<>();
                JButton button = new JButton(new AbstractAction("", new ImageIcon(add)) {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        SceneNodeComponent component = (SceneNodeComponent)comboBox.getSelectedItem();

                        if(component != null) {
                            try {
                                component.newInstance(UIGameEditor.getInstance().getScene(), node);
                                build(obj);
                            } catch(Exception ex) {
                                Log.put(0, ex);
                            }
                        } 
                    };
                });
                JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

                for(String name : SceneNodeComponentFactory.getNames()) {
                    comboBox.addItem(SceneNodeComponentFactory.getFactories().get(name));
                }
                flowPanel.add(comboBox);
                flowPanel.add(button);
                boxPanel.add(flowPanel);
            }
        } catch(Exception ex) {
            Log.put(0, ex);
        }

        getParent().validate();
    }
    
    public void enableUI() {
    }

    private Image load(String name) throws IOException {
        Image image = null;
        InputStream input = null;

        try {
            image = ImageIO.read(input = UIEditor.class.getResourceAsStream(name));
        } finally {
            if(input != null) {
                input.close();
            }
        }
        return image;
    }
}
