package org.game;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class UIPropertyEditor {
    
    public void add(SceneNodeComponent component, String name, JPanel panel) throws Exception {
        if(!name.startsWith("_")) {
            if(component.properties.containsKey(name)) {
                Class<?> cls = component.properties.get(name).getClass();

                if(Boolean.class.isAssignableFrom(cls)) {
                    addBoolean(component, name, panel);
                } else if(Integer.class.isAssignableFrom(cls)) {
                    addInt(component, name, panel);
                } else if(Float.class.isAssignableFrom(cls)) {
                    addFloat(component, name, panel);
                } else if(Double.class.isAssignableFrom(cls)) {
                    addDouble(component, name, panel);
                } else if(String.class.isAssignableFrom(cls)) {
                    addString(component, name, panel);
                } else if(Vec2.class.isAssignableFrom(cls)) {
                    addVec2(component, name, panel);
                } else if(Vec3.class.isAssignableFrom(cls)) {
                    addVec3(component, name, panel);
                } else if(Vec4.class.isAssignableFrom(cls)) {
                    addVec4(component, name, panel);
                } else if(UIButton.class.isAssignableFrom(cls)) {
                    addButton(component, name, panel);
                } else if(UIEnum.class.isAssignableFrom(cls)) {
                    addEnum(component, name, panel);
                }
            } else {
                Log.put(0, "component property '" + name + "' not found");
            }
        }
    }

    private void addBoolean(SceneNodeComponent component, String name, JPanel panel) throws Exception {
        JCheckBox checkBox = new JCheckBox();
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        checkBox.setSelected((Boolean)component.properties.get(name));
        checkBox.setText(name);

        flowPanel.add(checkBox);
        panel.add(flowPanel);

        checkBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    component.properties.put(name, checkBox.isSelected());
                } catch(Exception ex) {
                    Log.put(0, ex);
                }
            }
            
        });
    }

    private void addInt(SceneNodeComponent component, String name, JPanel panel) throws Exception {
        JTextField textField = new JTextField(15);
        JLabel label = new JLabel(name);
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        textField.setText(component.properties.get(name).toString());
        flowPanel.add(textField);
        flowPanel.add(label);
        panel.add(flowPanel);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    component.properties.put(name, Integer.parseInt(textField.getText().trim()));
                } catch(Exception ex) {
                }
            }
        });
    }

    private void addFloat(SceneNodeComponent component, String name, JPanel panel) throws Exception {
        JTextField textField = new JTextField(15);
        JLabel label = new JLabel(name);
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        textField.setText(component.properties.get(name).toString());
        flowPanel.add(textField);
        flowPanel.add(label);
        panel.add(flowPanel);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    component.properties.put(name, Float.parseFloat(textField.getText().trim()));
                } catch(Exception ex) {
                }
            }
        });
    }

    private void addDouble(SceneNodeComponent component, String name, JPanel panel) throws Exception {
        JTextField textField = new JTextField(15);
        JLabel label = new JLabel(name);
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        textField.setText(component.properties.get(name).toString());
        flowPanel.add(textField);
        flowPanel.add(label);
        panel.add(flowPanel);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    component.properties.put(name, Double.parseDouble(textField.getText().trim()));
                } catch(Exception ex) {
                }
            }
        });
    }

    private void addString(SceneNodeComponent component, String name, JPanel panel) throws Exception {
        JTextField textField = new JTextField(15);
        JLabel label = new JLabel(name);
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        textField.setText((String)component.properties.get(name));
        flowPanel.add(textField);
        flowPanel.add(label);
        panel.add(flowPanel);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    component.properties.put(name, textField.getText().trim());
                } catch(Exception ex) {
                    Log.put(0, ex);
                }
            }
        });
    }

    private void addVec2(SceneNodeComponent component, String name, JPanel panel) throws Exception {
        JTextField textField = new JTextField(15);
        JLabel label = new JLabel(name);
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        textField.setText(component.properties.get(name).toString());
        flowPanel.add(textField);
        flowPanel.add(label);
        panel.add(flowPanel);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    component.properties.put(name, Vec2.parse(textField.getText(), (Vec2)component.properties.get(name)));
                } catch(Exception ex) {
                }
            }
        });
    }

    private void addVec3(SceneNodeComponent component, String name, JPanel panel) throws Exception {
        JTextField textField = new JTextField(15);
        JLabel label = new JLabel(name);
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        textField.setText(component.properties.get(name).toString());
        flowPanel.add(textField);
        flowPanel.add(label);
        panel.add(flowPanel);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    component.properties.put(name, Vec3.parse(textField.getText(), (Vec3)component.properties.get(name)));
                } catch(Exception ex) {
                }
            }
        });
    }

    private void addVec4(SceneNodeComponent component, String name, JPanel panel) throws Exception {
        JTextField textField = new JTextField(15);
        JLabel label = new JLabel(name);
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        textField.setText(component.properties.get(name).toString());
        flowPanel.add(textField);
        flowPanel.add(label);
        panel.add(flowPanel);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    component.properties.put(name, Vec4.parse(textField.getText(), (Vec4)component.properties.get(name)));
                } catch(Exception ex) {
                }
            }
        });
    }

    private void addButton(SceneNodeComponent component, String name, JPanel panel) throws Exception {
        UIButton uiButton = (UIButton)component.properties.get(name);
        JButton button = new JButton(new AbstractAction(uiButton.text) {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                uiButton.clicked(component);
            }
        });
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        flowPanel.add(button);
        panel.add(flowPanel);
    }

    private void addEnum(SceneNodeComponent component, String name, JPanel panel) throws Exception {
        UIEnum uiEnum = (UIEnum)component.properties.get(name);
        JComboBox<String> combo = new JComboBox<>(uiEnum.list);
        JLabel label = new JLabel(name);
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        combo.setSelectedIndex(uiEnum.getValue());
        combo.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                try {
                    uiEnum.setValue(combo.getSelectedIndex());
                } catch(Exception ex) {
                    Log.put(0, ex);
                }
            }
        });

        flowPanel.add(combo);
        flowPanel.add(label);
        panel.add(flowPanel);
    }
}
