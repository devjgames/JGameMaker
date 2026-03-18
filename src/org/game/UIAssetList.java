package org.game;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class UIAssetList extends JList<String> {

    private JPopupMenu menu = new JPopupMenu();
    private Hashtable<String, JMenuItem> items = new Hashtable<>();
    private DefaultListModel<String> model = new DefaultListModel<>();
    private UIAssetList me;
    
    public UIAssetList() {
        setModel(model);

        me = this;

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        items.put("Import", new JMenuItem(new AbstractAction("Import") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();

                chooser.setMultiSelectionEnabled(true);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if(chooser.showOpenDialog(UIGameEditor.getInstance()) == JFileChooser.APPROVE_OPTION) {
                    for(File file : chooser.getSelectedFiles()) {
                        try {
                            Log.put(1, "importing '" + file.getName() + "' ...");
                            IO.writeAllBytes(IO.readAllBytes(file), IO.file(AssetManager.getRoot(), file.getName()));
                        } catch(Exception ex) {
                            Log.put(0, ex);
                        }
                    }
                    populate();
                    enableUI();
                }
            }
        }));
        menu.add(items.get("Import"));

        items.put("Import KFM", new JMenuItem(new AbstractAction("Import KFM") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();

                chooser.setMultiSelectionEnabled(false);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if(chooser.showOpenDialog(UIGameEditor.getInstance()) == JFileChooser.APPROVE_OPTION) {
                    File dir = chooser.getSelectedFile();

                    if(dir != null) {
                        try {
                            MD2.createMD2(dir);

                            populate();
                            enableUI();
                        } catch(Exception ex) {
                            Log.put(0, ex);
                        }
                    }
                }
            }
        }));
        menu.add(items.get("Import KFM"));

        items.put("Add KFM", new JMenuItem(new AbstractAction("Add KFM") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("KFM File", "mesh.kfm");

                if(name != null) {
                    try {
                        File file = IO.file(AssetManager.getRoot(), name);

                        if(IO.getExtension(file).equals(".kfm")) {
                            if(!file.exists()) {
                                IO.writeAllBytes(IO.readAllBytes(UIAssetList.class, "/org/game/resources/template.kfm"), file);
                                populate();
                                enableUI();
                            } else {
                                Log.put(0, ".kfm file already exists");
                            }
                        } else {
                            Log.put(0, "must have a .kfm extension");
                        }
                    } catch(Exception ex) {
                        Log.put(0, ex);
                    }
                }
            }
        }));
        menu.add(items.get("Add KFM"));

        items.put("Add JS", new JMenuItem(new AbstractAction("Add JS") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("JS File", "Component.js");

                if(name != null) {
                    name = name.trim();

                    for(int i = 0; i != name.length(); i++) {
                        char c = name.charAt(i);

                        if(i == 0) {
                            if(!Character.isLetter(c)) {
                                Log.put(0, "file name must begin with a letter");
                                return;
                            }
                        } else {
                            if(!Character.isLetter(c) && !Character.isDigit(c) && c != '_' && c != '.') {
                                Log.put(0, "file name must contain only letters, digits and underscores");
                                return;
                            }
                        }
                    }
                    try {
                        File file = IO.file(AssetManager.getRoot(), name);

                        if(IO.getExtension(file).equals(".js")) {
                            if(!file.exists()) {
                                String text = new String(IO.readAllBytes(UIAssetList.class, "/org/game/resources/template.js"));

                                text = text.replace("@FILE@", file.getName());
                                IO.writeAllBytes(text.getBytes(), file);
                                populate();
                                enableUI();
                            } else {
                                Log.put(0, ".js file already exists");
                            }
                        } else {
                            Log.put(0, "must have a .js extension");
                        }
                    } catch(Exception ex) {
                        Log.put(0, ex);
                    }
                }
            }
        }));
        menu.add(items.get("Add JS"));

        items.put("Add SCN", new JMenuItem(new AbstractAction("Add SCN") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog("SCN File", "scene1.scn");

                if(name != null) {
                    try {
                        File file = IO.file(AssetManager.getRoot(), name);

                        if(IO.getExtension(file).equals(".scn")) {
                            if(!file.exists()) {
                                IO.writeAllBytes("<scene/>".getBytes(), file);
                                populate();
                                enableUI();
                            } else {
                                Log.put(0, ".scn file already exists");
                            }
                        } else {
                            Log.put(0, "must have a .scn extension");
                        }
                    } catch(Exception ex) {
                        Log.put(0, ex);
                    }
                }
            }
        }));
        menu.add(items.get("Add SCN"));

        items.put("Delete", new JMenuItem(new AbstractAction("Delete") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(JOptionPane.showConfirmDialog(
                    UIGameEditor.getInstance(), 
                    "Delete? this can not be undone",
                    "Delete?",  
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                    IO.file(AssetManager.getRoot(), model.get(getSelectedIndex())).delete();

                    populate();
                    enableUI();
                }
            }
        }));
        menu.add(items.get("Delete"));

        items.put("Edit", new JMenuItem(new AbstractAction("Edit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String extension = getExtension();
                File file = IO.file(AssetManager.getRoot(), model.get(getSelectedIndex()));

                if(extension.equals(".js")) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch(Exception ex) {
                        Log.put(0, ex);
                    }
                } else if(extension.equals(".scn")) {
                    UIGameEditor.getInstance().loadScene(file);
                } else {
                    JDialog dialog = new JDialog(UIGameEditor.getInstance());
                    String text = "";

                    try {
                        text = new String(IO.readAllBytes(file));
                    } catch(Exception ex) {
                        Log.put(0, ex);
                        return;
                    }

                    dialog.setTitle(file.getName());
                    dialog.setModal(true);
                    dialog.setLayout(new BorderLayout());
                    dialog.setSize(600, 600);

                    JTextArea area = new JTextArea();
                    JScrollPane pane = new JScrollPane(area);
                    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
                    JButton button = new JButton(new AbstractAction("Save") {
                        public void actionPerformed(ActionEvent e) {
                            try {
                                IO.writeAllBytes(area.getText().getBytes(), file);
                            } catch(Exception ex) {
                                Log.put(0, ex);
                            }
                        };
                    });

                    area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

                    area.setText(text);

                    pane.setPreferredSize(new Dimension(400, 400));
                    dialog.add(pane, BorderLayout.CENTER);

                    panel.add(button);
                    dialog.add(panel, BorderLayout.SOUTH);

                    dialog.setVisible(true);
                }
            }
        }));
        menu.add(items.get("Edit"));

        items.put("Compile JS", new JMenuItem(new AbstractAction("Compile JS") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Scene scene = UIGameEditor.getInstance().getScene();

                    SceneNodeComponentFactory.compile();
                    if(scene != null) {
                        scene.root.reloadComponents(scene);
                    }
                    if(UIGameEditor.getInstance().getScene() != null) {
                        UIGameEditor.getInstance().getSceneTree().populate();
                    }
                } catch(Exception ex) {
                    Log.put(0, ex);
                }
            }
        }));
        menu.add(items.get("Compile JS"));

        items.put("Refresh", new JMenuItem(new AbstractAction("Refresh") {
            @Override
            public void actionPerformed(ActionEvent e) {
                populate();
                enableUI();
            }
        }));
        menu.add(items.get("Refresh"));

        items.put("Set Log Level", new JMenuItem(new AbstractAction("Set Log Level") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object level = JOptionPane.showInputDialog("Level", "" + Log.level);

                if(level != null) {
                    try {
                        Log.level = Integer.parseInt(level.toString().trim());
                    } catch(Exception ex) {
                        Log.put(0, ex);
                    }
                }
            };
        }));
        menu.add(items.get("Set Log Level"));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3) {
                    menu.show(me, e.getX(), e.getY());
                }
            }
        });

        populate();

        addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                enableUI();
            }
            
        });
    }

    public void enableUI() {
        UIGameEditor gameEditor = UIGameEditor.getInstance();
        boolean enabled = !gameEditor.isPlaying();

        for(String key : items.keySet()) {
            items.get(key).setEnabled(enabled);
        }

        String extension = getExtension();

        if(extension != null) {
            items.get("Edit").setEnabled(
                enabled && (
                    extension.equals(".kfm") || 
                    extension.equals(".js") || 
                    extension.equals(".scn") ||
                    extension.equals(".txt")
                    ));
        } else {
            items.get("Edit").setEnabled(false);
            items.get("Delete").setEnabled(false);
        }
    }

    private String getExtension() {
        int i = getSelectedIndex();

        if(i >= 0 && i < model.size()) {
            File file = IO.file(model.get(i));
            
            return IO.getExtension(file);
        }
        return null;
    }

    private void populate() {
        File[] files = AssetManager.getRoot().listFiles();
        Vector<String> names = new Vector<>();

        if(files != null) {
            for(File file : files) {
                if(file.isFile() && !file.getName().equals(".DS_Store")) {
                    names.add(file.getName());
                }
            }
        }
        names.sort((a, b) -> {
            String e1 = IO.getExtension(IO.file(a));
            String e2 = IO.getExtension(IO.file(b));

            if(e1.equals(e2)) {
                return a.compareTo(b);
            } else {
                return e1.compareTo(e2);
            }
        });

        model.removeAllElements();

        for(String name : names) {
            model.addElement(name);
        }
    }
}
