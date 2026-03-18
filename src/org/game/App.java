package org.game;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.UIManager;


// javadoc -d Docs -sourcepath src -linksource --ignore-source-errors  org.game
public class App {

    private static boolean started = false;

    public static void main(String[] args) throws Exception {
        if(started) {
            return;
        }
        
        started = true;

        String os = System.getProperty("os.name").toLowerCase();

        if(os.startsWith("windows")) {
            System.setProperty("org.lwjgl.librarypath", IO.file("./natives/windows").getCanonicalFile().getAbsolutePath());
        } else if(os.startsWith("mac")) {
            System.setProperty("org.lwjgl.librarypath", IO.file("./natives/macosx").getCanonicalFile().getAbsolutePath());
        } else {
            System.setProperty("org.lwjgl.librarypath", IO.file("./natives/linux").getCanonicalFile().getAbsolutePath());
        }

        String text = new String(IO.readAllBytes(IO.file("Config.txt"))).trim();

        if(text.equals("theme dark")) {
            DarkTheme.setLookAndFeel();
        } else {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        if(args.length == 2) {
            AssetManager.root = IO.file(args[0]);
            Scene[] scene = new Scene[1];
            String[] name = new String[] { args[1] };

            new Game(800, 600, null, new Game.GameLoop() {

                @Override
                public void init() throws Exception {
                    scene[0] = SceneSerializer.deserialize(false, IO.file(AssetManager.getRoot(), name[0]));
                }

                @Override
                public void render() throws Exception {
                    if(scene[0] != null) {
                        Game.getInstance().getSceneRenderer().render(scene[0]);

                        scene[0] = Scene.next(scene[0]);
                    }
                }
            
            });
        } else {
            JFileChooser chooser = new JFileChooser(IO.file(".").getAbsoluteFile());

            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();

                if(file.isDirectory()) {
                    AssetManager.root = file;
                }
            }

            if(AssetManager.root != null) {
                new UIGameEditor(600, 400);
            } else {
                System.out.println("directory not selected");
                System.exit(0);
            }
        }
    }
}
