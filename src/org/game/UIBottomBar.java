package org.game;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

class UIBottomBar extends JPanel {
    
    private Hashtable<String, JButton> buttons = new Hashtable<>();
    private Hashtable<String, JToggleButton> toggleButtons = new Hashtable<>();

    public UIBottomBar() {
        super(new FlowLayout(FlowLayout.LEFT, 5, 5));

        buttons.put("Save", new JButton(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Scene scene = UIGameEditor.getInstance().getScene();

                try {
                    SceneSerializer.serialize(scene, scene.file);
                } catch(Exception ex) {
                    Log.put(0, ex);
                }
            }
        }));
        add(buttons.get("Save"));

        buttons.put("Play", new JButton(new AbstractAction("Play") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor.getInstance().play();
            }
        }));
        add(buttons.get("Play"));

        buttons.put("Start", new JButton(new AbstractAction("Start") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor.getInstance().start();
            }
        }));
        add(buttons.get("Start"));

        buttons.put("Edit Scene", new JButton(new AbstractAction("Edit Scene") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor.getInstance().getEditor().build(UIGameEditor.getInstance().getScene());
                UIGameEditor.getInstance().getTabbedPane().setSelectedIndex(1);
            }
        }));
        add(buttons.get("Edit Scene"));

        buttons.put("Clear", new JButton(new AbstractAction("Clear") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor.getInstance().getConsoleArea().setText("");
            }
        }));
        add(buttons.get("Clear"));

        toggleButtons.put("Pause", new JToggleButton(new AbstractAction("Pause") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Log.paused = !Log.paused;
            }
        }));
        add(toggleButtons.get("Pause"));

        buttons.put("Target To", new JButton(new AbstractAction("Target To") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor gameEditor = UIGameEditor.getInstance();
                Scene scene = gameEditor.getScene();
                Vec3 x = scene.calcOffset();

                scene.target.set(gameEditor.getSelection().absolutePosition);
                scene.target.add(x, scene.eye);
            }
        }));
        add(buttons.get("Target To"));

        buttons.put("To Target", new JButton(new AbstractAction("To Target") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor gameEditor = UIGameEditor.getInstance();
                Scene scene = gameEditor.getScene();

                gameEditor.getSelection().position.set(scene.target);
            }
        }));
        add(buttons.get("To Target"));

        buttons.put("Zero Target", new JButton(new AbstractAction("Zero Target") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor gameEditor = UIGameEditor.getInstance();
                Scene scene = gameEditor.getScene();
                Vec3 x = scene.calcOffset();

                scene.target.toZero();
                scene.target.add(x, scene.eye);
            }
        }));
        add(buttons.get("Zero Target"));

        buttons.put("Zero Rot", new JButton(new AbstractAction("Zero Rot") {
            @Override
            public void actionPerformed(ActionEvent e) {
                SceneNode node = UIGameEditor.getInstance().getSelection();

                node.r.set(1, 0, 0);
                node.u.set(0, 1, 0);
                node.f.set(0, 0, 1);
            }
        }));
        add(buttons.get("Zero Rot"));

        buttons.put("Rot X 45", new JButton(new AbstractAction("Rot X 45") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor.getInstance().getSelection().rotate(0, 45);
            }
        }));
        add(buttons.get("Rot X 45"));

        buttons.put("Rot Y 45", new JButton(new AbstractAction("Rot Y 45") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor.getInstance().getSelection().rotate(1, 45);
            }
        }));
        add(buttons.get("Rot Y 45"));

        buttons.put("Rot Z 45", new JButton(new AbstractAction("Rot Z 45") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor.getInstance().getSelection().rotate(2, 45);
            }
        }));
        add(buttons.get("Rot Z 45"));

        buttons.put("Unit Scale", new JButton(new AbstractAction("Unit Scale") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor.getInstance().getSelection().scale.set(1, 1, 1);
            }
        }));
        add(buttons.get("Unit Scale"));
    }

    public void enableUI() {
        boolean enabled = UIGameEditor.getInstance().getScene() != null;
        boolean isPlaying = UIGameEditor.getInstance().isPlaying();
        boolean hasSelection = UIGameEditor.getInstance().getSelection() != null;

        buttons.get("Save").setEnabled(enabled && !isPlaying);
        buttons.get("Play").setEnabled(enabled && !isPlaying);
        buttons.get("Start").setEnabled(enabled);
        buttons.get("Edit Scene").setEnabled(enabled && !isPlaying);
        buttons.get("Target To").setEnabled(enabled && !isPlaying && hasSelection);
        buttons.get("To Target").setEnabled(enabled && !isPlaying && hasSelection);
        buttons.get("Zero Target").setEnabled(enabled && !isPlaying);
        buttons.get("Zero Rot").setEnabled(enabled && !isPlaying && hasSelection);
        buttons.get("Rot X 45").setEnabled(enabled && !isPlaying && hasSelection);
        buttons.get("Rot Y 45").setEnabled(enabled && !isPlaying && hasSelection);
        buttons.get("Rot Z 45").setEnabled(enabled && !isPlaying && hasSelection);
        buttons.get("Unit Scale").setEnabled(enabled && !isPlaying && hasSelection);
    }
}
