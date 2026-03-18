package org.game;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class UIFieldEditor {
    
    public void add(Object obj, Field field, JPanel panel) throws Exception {
        if(field.getAnnotation(Hidden.class) != null) {
            return;
        }

        int m = field.getModifiers();

        if(!Modifier.isPublic(m) || Modifier.isStatic(m)) {
            return;
        }

        Class<?> cls = field.getType();

        if(boolean.class.isAssignableFrom(cls)) {
            addBoolean(obj, field, panel);
        } else if(int.class.isAssignableFrom(cls)) {
            addInt(obj, field, panel);
        } else if(float.class.isAssignableFrom(cls)) {
            addFloat(obj, field, panel);
        } else if(String.class.isAssignableFrom(cls)) {
            addString(obj, field, panel);
        } else if(Vec2.class.isAssignableFrom(cls)) {
            addVec2(obj, field, panel);
        } else if(Vec3.class.isAssignableFrom(cls)) {
            addVec3(obj, field, panel);
        } else if(Vec4.class.isAssignableFrom(cls)) {
            addVec4(obj, field, panel);
        } else if(cls.isEnum()) {
            addEnum(obj, field, panel);
        }  
    }

    private void addBoolean(Object obj, Field field, JPanel panel) throws Exception {
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JCheckBox checkBox = new JCheckBox();

        checkBox.setSelected((Boolean)field.get(obj));
        checkBox.setText(field.getName());

        flowPanel.add(checkBox);
        panel.add(flowPanel);

        checkBox.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    field.set(obj, checkBox.isSelected());
                } catch(Exception ex) {
                    Log.put(0, ex);
                }
            }
            
        });
    }

    private void addInt(Object obj, Field field, JPanel panel) throws Exception {
        JTextField textField = new JTextField(15);
        JLabel label = new JLabel(field.getName());
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        textField.setText(field.get(obj).toString());
        flowPanel.add(textField);
        flowPanel.add(label);
        panel.add(flowPanel);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    field.set(obj, Integer.parseInt(textField.getText().trim()));
                } catch(Exception ex) {
                }
            }
        });
    }

    private void addFloat(Object obj, Field field, JPanel panel) throws Exception {
        JTextField textField = new JTextField(15);
        JLabel label = new JLabel(field.getName());
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        textField.setText(field.get(obj).toString());
        flowPanel.add(textField);
        flowPanel.add(label);
        panel.add(flowPanel);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    field.set(obj, Float.parseFloat(textField.getText().trim()));
                } catch(Exception ex) {
                }
            }
        });
    }

private void addString(Object obj, Field field, JPanel panel) throws Exception {
        JTextField textField = new JTextField(15);
        JLabel label = new JLabel(field.getName());
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        textField.setText((String)field.get(obj));
        flowPanel.add(textField);
        flowPanel.add(label);
        panel.add(flowPanel);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    field.set(obj, textField.getText().trim());
                    if(obj instanceof SceneNode && field.getName().equals("name")) {
                        JTree tree = UIGameEditor.getInstance().getSceneTree();
                        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();

                        model.nodeChanged((DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent());
                    }
                } catch(Exception ex) {
                    Log.put(0, ex);
                }
            }
        });
    }

    private void addVec2(Object obj, Field field, JPanel panel) throws Exception {
        JTextField textField = new JTextField(15);
        JLabel label = new JLabel(field.getName());
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        textField.setText(field.get(obj).toString());
        flowPanel.add(textField);
        flowPanel.add(label);
        panel.add(flowPanel);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    field.set(obj, Vec2.parse(textField.getText(), (Vec2)field.get(obj)));
                } catch(Exception ex) {
                }
            }
        });
    }

    private void addVec3(Object obj, Field field, JPanel panel) throws Exception {
        JTextField textField = new JTextField(15);
        JLabel label = new JLabel(field.getName());
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        textField.setText(field.get(obj).toString());
        flowPanel.add(textField);
        flowPanel.add(label);
        panel.add(flowPanel);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    field.set(obj, Vec3.parse(textField.getText(), (Vec3)field.get(obj)));
                } catch(Exception ex) {
                }
            }
        });
    }

    private void addVec4(Object obj, Field field, JPanel panel) throws Exception {
        JTextField textField = new JTextField(15);
        JLabel label = new JLabel(field.getName());
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        textField.setText(field.get(obj).toString());
        flowPanel.add(textField);
        flowPanel.add(label);
        panel.add(flowPanel);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    field.set(obj, Vec4.parse(textField.getText(), (Vec4)field.get(obj)));
                } catch(Exception ex) {
                }
            }
        });
    }

    private void addEnum(Object obj, Field field, JPanel panel) throws Exception {
        JComboBox<String> comboBox = new JComboBox<>();
        JLabel label = new JLabel(field.getName());
        JPanel flowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        Object[] constants = field.getType().getEnumConstants();

        for(Object c : constants) {
            comboBox.addItem(c.toString());
        }
        comboBox.setSelectedItem(field.get(obj).toString());
        flowPanel.add(comboBox);
        flowPanel.add(label);
        panel.add(flowPanel);

        comboBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                try {
                    field.set(obj, constants[comboBox.getSelectedIndex()]);
                } catch(Exception ex) {
                    Log.put(0, ex);
                }
            }
            
        });
    }
}
