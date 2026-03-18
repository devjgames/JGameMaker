package org.game;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.game.Game.GameLoop;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.Vector;

class UIGameEditor extends JFrame implements GameLoop {

    private static UIGameEditor instance = null;

    public static UIGameEditor getInstance() {
        return instance;
    }

    private UISceneTree tree;
    private UIAssetList assetList;
    private UIEditor editor;
    private UITopBar topBar;
    private UIBottomBar bottomBar;
    private Canvas canvas = new Canvas();
    private JTextArea consoleArea;
    private Game game;
    private Scene scene = null;
    private SceneNode clipboard = null;
    private SceneNode setClipboard = null;
    private SceneNode pasteNode = null;
    private JTabbedPane tabbedPane;
    private boolean isPlaying = false;
    private File loadSceneFile = null;
    private boolean takeShot = false;
    private String addAsset = null;
    private boolean down = false;
    private boolean down2 = false;
    private boolean play = false;
    private final Vector<SceneNode> brushes = new Vector<>();
    private int brush = -1;
    private boolean initBrushes = false;
    private final Vec3 origin = new Vec3();
    private final Vec3 direction = new Vec3();
    private boolean stopPainting = false;
    
    public UIGameEditor(int width, int height) throws Exception {
        instance = this;

        setTitle("JGameMaker");
        setResizable(true);
        setLayout(new BorderLayout());

        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setFocusable(true);
        canvas.requestFocus();

        add(canvas, BorderLayout.CENTER);

        topBar = new UITopBar();
        add(topBar, BorderLayout.NORTH);

        tree = new UISceneTree();
        assetList = new UIAssetList();
        editor = new UIEditor();

        JScrollPane treePane = new JScrollPane(tree);

        treePane.setPreferredSize(new Dimension(250, 100));
        add(treePane, BorderLayout.WEST);

        tabbedPane = new JTabbedPane();

        JScrollPane assetListPane = new JScrollPane(assetList);
        JScrollPane editorPane = new JScrollPane(editor);

        tabbedPane.setPreferredSize(new Dimension(350, 100));
        tabbedPane.add("Assets", assetListPane);
        tabbedPane.add("Editor", editorPane);
        add(tabbedPane, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomBar = new UIBottomBar();
        bottomPanel.add(bottomBar, BorderLayout.NORTH);

        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane consolePane = new JScrollPane(consoleArea);

        consolePane.setPreferredSize(new Dimension(100, 300));
        bottomPanel.add(consolePane, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        enableUI();

        pack();

        setVisible(true);

        game = new Game(width, height, canvas, this);
        game.addWindowListener(this);
    }

    public JTextArea getConsoleArea() {
        return consoleArea;
    }

    public Scene getScene() {
        return scene;
    }

    public void loadScene(File file) {
        loadSceneFile = file;
    }

    public void play() {
        play = true;
    }

    public void start() {
        try {
            new ProcessBuilder()
                .inheritIO()
                .command("java",  "-jar", "JGameMaker.jar", AssetManager.getRoot().getPath(), scene.file.getName())
                .start();
        } catch(Exception ex) {
            Log.put(0, ex);
        }
    }

    public void startPainting() {
        initBrushes = true;
    }

    public void nextBrush() {
        if(brush != -1) {
            brush = (brush + 1) % brushes.size();
            
            SceneNode node = brushes.get(brush);

            node.r.set(1, 0, 0);
            node.u.set(0, 1, 0);
            node.f.set(0, 0, 1);
        }
    }

    public void stopPainting() {
        stopPainting = true;
    }

    public void takeShot() {
        takeShot = true;
    }

    public void addAsset(String file) {
        addAsset = file;
    }

    public SceneNode getSelection() {
        TreePath path = tree.getSelectionPath();

        if(path != null) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)path.getLastPathComponent();

            return (SceneNode)treeNode.getUserObject();
        }
        return null;
    }

    public SceneNode getClipboard() {
        return clipboard;
    }

    public void setClipboard(SceneNode node) {
        setClipboard = node;
    }

    public void pasteNode() {
        pasteNode = clipboard;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public UISceneTree getSceneTree() {
        return tree;
    }

    public UIAssetList getAssetList() {
        return assetList;
    }

    public UIEditor getEditor() {
        return editor;
    }
    
    public UITopBar getTopBar() {
        return topBar;
    }

    public UIBottomBar getBottomBar() {
        return bottomBar;
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void render() throws Exception {
        if(loadSceneFile != null) {
            File file = loadSceneFile;

            loadSceneFile = null;
            try {
                if(scene != null) {
                    scene.brush = null;
                    brushes.clear();
                    brush = -1;
                }
                game.getAssets().clear();
                scene = null;
                scene = SceneSerializer.deserialize(true, file);
            } catch(Exception ex) {
                Log.put(0, ex);
            }
            setTitle("JGameMaker - " + file.getName());
            tree.populate();
        } else if(takeShot) {
            takeShot = false;
            try {
                game.getSceneRenderer().takeShot(scene);
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        } else if(setClipboard != null) {
            try {
                SceneNode node = setClipboard;

                setClipboard = null;
                clipboard = new SceneNode(scene,  node);

                enableUI();
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        } else if(addAsset != null) {
            try {
                Renderable renderable = null;
                File file = IO.file(addAsset);
                String extension = IO.getExtension(file);

                addAsset = null;

                if(extension.equals(".obj") || extension.equals(".msh")) {
                    Mesh mesh = Game.getInstance().getAssets().load(file);

                    renderable = mesh.newInstance();
                } else if(extension.equals(".kfm")) {
                    KeyFrameMesh mesh = Game.getInstance().getAssets().load(file);

                    renderable = mesh.newInstance();
                } else {
                    ParticleSystem particles = Game.getInstance().getAssets().load(file);

                    renderable = particles.newInstance();
                }

                SceneNode node = new SceneNode();

                node.name = renderable.getFile().getName();
                node.renderable = renderable;

                tree.addNode(node);
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        } else if(pasteNode != null) {
            try {
                pasteNode = null;
                tree.addNode(new SceneNode(scene, clipboard));
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        } else if(play) {
            play = false;
            try {
                File file = scene.file;

                State.properties.clear();

                if(scene != null) {
                    scene.brush = null;
                    brushes.clear();
                    brush = -1;
                }

                game.getAssets().clear();
                scene = null;
                scene = SceneSerializer.deserialize(false, file);
                isPlaying = true;
                setTitle("JGameMaker - " + file.getName() + ", ESC stop");
                tree.populate();
            } catch(Exception ex) {
                Log.put(0, ex);
                scene = null;
                setTitle("JGameMaker");
                tree.populate();
            }
            enableUI();
        } else if(initBrushes) {
            initBrushes = false;
            try {
                scene.brush = null;
                brush = -1;
                brushes.clear();
                scene.root.addBrushes(scene, brushes);
                if(!brushes.isEmpty()) {
                    brush = 0;
                }
                for(SceneNode node : brushes) {
                    node.r.set(1, 0, 0);
                    node.u.set(0, 1, 0);
                    node.f.set(0, 0, 1);
                }
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        } else if (stopPainting) {
            stopPainting = false;
            brush = -1;
            brushes.clear();
            scene.brush = null;
        }
        if(scene == null) {
            GFX.clear(0.15f, 0.15f, 0.15f, 1);
        } else {
            try {
                game.getSceneRenderer().render(scene);

                if(scene.brush != null) {
                    scene.brush.visible = false;
                }
                scene.brush = null;

                if(isPlaying) {
                    scene = Scene.next(scene);

                    if(game.keyDown(Keys.KEY_ESCAPE)) {
                        game.setMouseGrabbed(false);
                        try {
                            File file = scene.file;

                            game.getAssets().clear();
                            scene = null;
                            scene = SceneSerializer.deserialize(true, file);
                        } catch(Exception ex) {
                            Log.put(0, ex);
                        }
                        setTitle("JGameMaker - " + scene.file.getName());
                        isPlaying = false;
                        tree.populate();
                        enableUI();

                        State.properties.clear();
                    }
                } else {
                    if(brush != -1) {
                        int x = game.mouseX();
                        int y = game.h() - game.mouseY() - 1;
                        
                        GFX.unproject(x, y, 0, 0, 0, game.w(), game.h(), scene.projection, scene.view, origin);
                        GFX.unproject(x, y, 1, 0, 0, game.w(), game.h(), scene.projection, scene.view, direction);

                        direction.sub(origin).normalize();

                        float time = direction.dot(0, 1, 0);

                        if(Math.abs(time) > 0.0000001) {
                            time = -origin.dot(0, 1, 0) / time;

                            Vec3 ipoint = new Vec3(direction).scale(time).add(origin);
                            int snap = scene.snap;

                            if(snap > 0) {
                                ipoint.x = Math.round(ipoint.x / snap) * snap;
                                ipoint.z = Math.round(ipoint.z / snap) * snap;
                            }

                            scene.brush = brushes.get(brush);
                            scene.brush.visible = true;
                            scene.brush.position.set(ipoint.x, scene.brush.position.y, ipoint.z);
                        }
                    }
                    handleInput();
                }
            } catch(Exception ex) {
                Log.put(0, ex);
            }
        }
    }

    public void enableUI() {
        tree.enabledUI();
        topBar.enableUI();
        bottomBar.enableUI();
        assetList.enableUI();
        editor.enableUI();
    }

    private void handleInput() throws Exception {
        int mode = topBar.getMode();
        SceneNode selection = getSelection();

        if(game.buttonDown(0)) {
            if(mode == UITopBar.ZOOM) {
                scene.zoom(game.dY());
            } else if(mode == UITopBar.ROTATE) {
                scene.rotateAroundTarget(-game.dX(), game.dY());
            } else if(mode == UITopBar.PANXZ) {
                scene.move(scene.target, game.dX(), -game.dY());
            } else if(mode == UITopBar.PANY) {
                scene.move(scene.target, -game.dY());
            } else if(mode == UITopBar.SELECT) {
                if(!down) {
                    int x = game.mouseX();
                    int y = game.h() - game.mouseY() - 1;

                    GFX.unproject(x, y, 0, 0, 0, game.w(), game.h(), scene.projection, scene.view, origin);
                    GFX.unproject(x, y, 1, 0, 0, game.w(), game.h(), scene.projection, scene.view, direction);

                    float[] time = new float[] { Float.MAX_VALUE };
                    SceneNode[] node = new SceneNode[] { null };
                    Triangle triangle = new Triangle();

                    direction.sub(origin).normalize();

                    scene.root.traverse((n) -> {
                        if(n.renderable != null) {
                            for(int i = 0; i != n.renderable.getTriangleCount(); i++) {
                                n.renderable.getTriangle(scene, n, i, triangle);
                                triangle.transform(n.model);
                                if(direction.dot(triangle.n) < 0) {
                                    if(triangle.intersects(origin, direction, 0, time)) {
                                        node[0] = n;
                                    }
                                }   
                            }
                        } else if(n.emitsLight || n.isLocation) {
                            if(scene.isectPoint(n.absolutePosition.x, n.absolutePosition.y, n.absolutePosition.z, 8, time)) {
                                node[0] = n;
                            }
                        }
                        return true;
                    });

                    if(node[0] == null) {
                        tree.clearSelection();
                    } else {
                        tree.setSelection(node[0]);
                    }
                    enableUI();
                }
            } else if(mode == UITopBar.PAINT) {
                if(brush != -1) {
                    int x = game.mouseX();
                    int y = game.h() - game.mouseY() - 1;
                    
                    GFX.unproject(x, y, 0, 0, 0, game.w(), game.h(), scene.projection, scene.view, origin);
                    GFX.unproject(x, y, 1, 0, 0, game.w(), game.h(), scene.projection, scene.view, direction);

                    direction.sub(origin).normalize();

                    float[] time = new float[] { direction.dot(0, 1, 0) };

                    if(Math.abs(time[0]) > 0.0000001) {
                        time[0] = -origin.dot(0, 1, 0) / time[0];

                        Vec3 ipoint = new Vec3(direction).scale(time[0]).add(origin);
                        Triangle triangle = new Triangle();
                        int snap = scene.snap;

                        if(snap > 0) {
                            ipoint.x = Math.round(ipoint.x / snap) * snap;
                            ipoint.z = Math.round(ipoint.z / snap) * snap;
                        }

                        scene.brush = brushes.get(brush);
                        scene.brush.position.set(ipoint.x, scene.brush.position.y, ipoint.z);

                        if(!down) {
                            for(SceneNode node : scene.root) {
                                if(node.name.equals(scene.canvasNode)) {
                                    if(game.keyDown(Keys.KEY_LSHIFT)) {
                                        SceneNode[] hit = new SceneNode[] { null };

                                        time[0] = Float.MAX_VALUE;
                                        for(SceneNode tile : node) {
                                            tile.traverse((n) -> {
                                                if(n.renderable != null) {
                                                    for(int i = 0; i != n.renderable.getTriangleCount(); i++) {
                                                        n.renderable.getTriangle(scene, n, i, triangle);
                                                        triangle.transform(n.model);
                                                        if(direction.dot(triangle.n) < 0) {
                                                            if(triangle.intersects(origin, direction, 0, time)) {
                                                                hit[0] = tile;
                                                            }
                                                        }   
                                                    }
                                                } else if(n.emitsLight || n.isLocation) {
                                                    if(scene.isectPoint(n.absolutePosition.x, n.absolutePosition.y, n.absolutePosition.z, 8, time)) {
                                                        hit[0] = tile;
                                                    }
                                                }
                                                return true;
                                            });
                                        }
                                        if(hit[0] != null) {
                                            hit[0].detach();
                                            tree.clearSelection();
                                            tree.populate();
                                        } 
                                    } else {
                                        node.addChild(new SceneNode(scene, scene.brush));
                                        tree.populate();
                                        tree.setSelection(node.getChild(node.getChildCount() - 1));
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if(selection != null) {
                Mat4 m = new Mat4(selection.getParent().model).invert();

                if(mode == UITopBar.MOVXZ) {
                    scene.move(selection.position, game.dX(), -game.dY(), m);
                } else if(mode == UITopBar.MOVY) {
                    scene.move(selection.position, -game.dY(), m);
                } else if(mode == UITopBar.ROTX) {
                    selection.rotate(0, game.dX());
                } else if(mode == UITopBar.ROTY) {
                    selection.rotate(1, game.dX());
                } else if(mode == UITopBar.ROTZ) {
                    selection.rotate(2, game.dX());
                } else if(mode == UITopBar.SCALE) {
                    if(game.dY() < 0) {
                        selection.scale.scale(0.9f);
                    } else if(game.dY() > 0) {
                        selection.scale.scale(1.1f);
                    }
                }
            }
            down = true;
        } else {
            if((mode == UITopBar.MOVXZ || mode == UITopBar.MOVY) && down && selection != null) {
                int snap = scene.snap;

                if(snap > 0) {
                    Vec3 p = selection.position;

                    p.x = Math.round(p.x / snap) * snap;
                    p.y = Math.round(p.y / snap) * snap;
                    p.z = Math.round(p.z / snap) * snap;
                }
            }
            down = false;
        }
        if(game.buttonDown(1)) {
            if(!down2) {
                if(mode == UITopBar.PAINT && brush != -1) {
                    SceneNode node = brushes.get(brush);

                    node.rotate(1, 45);
                }
                down2 = true;
            }
        } else {
            down2 = false;
        }
    }
}
