package org.game;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

class UISceneTree extends JTree {

    private DefaultTreeModel model;
    private Hashtable<String, JMenuItem> items = new Hashtable<>();
    private JPopupMenu menu = new JPopupMenu();
    private UISceneTree me;

    public UISceneTree() {
        model = new DefaultTreeModel(new DefaultMutableTreeNode());
        setModel(model);
        setRootVisible(false);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);;
        
        me = this;

        items.put("Add Mesh", new JMenuItem(new AbstractAction("Add Mesh") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAsset(new String[] { ".obj", ".msh" }, "Meshes", "Add Mesh");
            }
        }));
        menu.add(items.get("Add Mesh"));

        items.put("Add KFM", new JMenuItem(new AbstractAction("Add KFM") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAsset(new String[] { ".kfm" }, "KFM", "Add KFM");
            }
        }));
        menu.add(items.get("Add KFM"));

        items.put("Add Node", new JMenuItem(new AbstractAction("Add Node") {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNode(new SceneNode());
            }
        }));
        menu.add(items.get("Add Node"));

        items.put("Cut Node", new JMenuItem(new AbstractAction("Cut Node") {
            @Override
            public void actionPerformed(ActionEvent e) {
                SceneNode node = UIGameEditor.getInstance().getSelection();

                UIGameEditor.getInstance().setClipboard(node);
                node.detach();
                populate();
            }
        }));
        menu.add(items.get("Cut Node"));

        items.put("Copy Node", new JMenuItem(new AbstractAction("Copy Node") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor.getInstance().setClipboard(UIGameEditor.getInstance().getSelection());
            }
        }));
        menu.add(items.get("Copy Node"));

        items.put("Paste Node", new JMenuItem(new AbstractAction("Paste Node") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor.getInstance().pasteNode();
            }
        }));
        menu.add(items.get("Paste Node"));

        items.put("Delete Node", new JMenuItem(new AbstractAction("Delete Node") {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIGameEditor.getInstance().getSelection().detach();
                populate();
            }
        }));
        menu.add(items.get("Delete Node"));

        items.put("Refresh", new JMenuItem(new AbstractAction("Refresh") {
            @Override
            public void actionPerformed(ActionEvent e) {
                populate();
            }
        }));
        menu.add(items.get("Refresh"));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3) {
                    menu.show(me, e.getX(), e.getY());
                }
            }
        });

        addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                SceneNode node = UIGameEditor.getInstance().getSelection();

                if(node != null) {
                    UIGameEditor.getInstance().getEditor().build(node);
                    UIGameEditor.getInstance().getTabbedPane().setSelectedIndex(1);
                } else {
                    UIGameEditor.getInstance().getEditor().build(null);
                }
                UIGameEditor.getInstance().enableUI();
            }   
            
        });
    }
    
    public void enabledUI() {
        Scene scene = UIGameEditor.getInstance().getScene();
        boolean isPlaying = UIGameEditor.getInstance().isPlaying();
        SceneNode selection = UIGameEditor.getInstance().getSelection();
        SceneNode clipboard = UIGameEditor.getInstance().getClipboard();

        items.get("Add Mesh").setEnabled(scene != null && !isPlaying);
        items.get("Add KFM").setEnabled(scene != null && !isPlaying);
        items.get("Add Node").setEnabled(scene != null && !isPlaying);
        items.get("Cut Node").setEnabled(scene != null && !isPlaying && selection != null);
        items.get("Copy Node").setEnabled(scene != null && !isPlaying && selection != null);
        items.get("Paste Node").setEnabled(scene != null && !isPlaying && clipboard != null);
        items.get("Delete Node").setEnabled(scene != null && !isPlaying && selection != null);
        items.get("Refresh").setEnabled(scene != null && !isPlaying);
    }

    public void setSelection(SceneNode node) {
        if(node != null) {
            setSelection(node, (DefaultMutableTreeNode)model.getRoot());
        } else {
            clearSelection();
        }
        UIGameEditor.getInstance().enableUI();
    }

    private void setSelection(SceneNode node, DefaultMutableTreeNode treeNode) {
        if(node == treeNode.getUserObject()) {
            setSelectionPath(new TreePath(treeNode.getPath()));
        } else {
            for(int i = 0; i != treeNode.getChildCount(); i++) {
                setSelection(node, (DefaultMutableTreeNode)treeNode.getChildAt(i));
            }
        }
    }

    public void populate() {
        Scene scene = UIGameEditor.getInstance().getScene();

        if(scene != null && !UIGameEditor.getInstance().isPlaying()) {
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode();

            clearSelection();
            for(SceneNode node : scene.root) {
                populate(treeNode, node);
            }

            model.setRoot(treeNode);
        } else {
            model.setRoot(new DefaultMutableTreeNode());
        }

        UIGameEditor.getInstance().getEditor().build(null);
        UIGameEditor.getInstance().enableUI();
    }

    private void populate(DefaultMutableTreeNode parentNode, SceneNode node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);

        parentNode.add(treeNode);
        for(SceneNode child : node) {
            populate(treeNode, child);
        }
    }

    private void addAsset(String[] extensions, String message, String title) {
        Vector<File> files = new Vector<>();
        File[] list = AssetManager.getRoot().listFiles();

        if(list != null) {
            for(File file : list) {
                for(String extension : extensions) {
                    if(IO.getExtension(file).equals(extension)) {
                        files.add(file);
                        break;
                    }
                }
            }
        }
        if(!files.isEmpty()) {
            Collections.sort(files);
            Object[] items = new Object[files.size()];

            for(int i = 0; i != items.length; i++) {
                items[i] = files.get(i).getName();
            }

            if(items != null) {
                Object r = JOptionPane.showInputDialog(
                    UIGameEditor.getInstance(), 
                    message, title, JOptionPane.QUESTION_MESSAGE, 
                    null, items, items[0]
                    );

                if(r != null) {
                    UIGameEditor.getInstance().addAsset((String)r);
                }
            }
        }
    }

    public void addNode(SceneNode node) {
        SceneNode parent = UIGameEditor.getInstance().getSelection();

        if(parent == null) {
            parent = UIGameEditor.getInstance().getScene().root;
        }
        parent.addChild(node);
        populate();
        setSelection(node);
        UIGameEditor.getInstance().enableUI();
    }
}
