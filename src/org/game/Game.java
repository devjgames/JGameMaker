package org.game;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import java.awt.Canvas;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class Game {

    static interface GameLoop {
        
        void init() throws Exception;

        void render() throws Exception;
    }

    private static Game instance = null;
    
    public static Game getInstance() {
        return instance;
    }

    private boolean running = true;
    private ResourceManager resources = new ResourceManager();
    private AssetManager assets;
    private HashSet<Renderer> renderers = new HashSet<>();
    private double lastTime = 0;
    private double totalTime = 0;
    private double elapsedTime = 0;
    private double seconds = 0;
    private int frames = 0;
    private int fps = 0;
    private int dX = 0;
    private int dY = 0;
    private boolean reverseY = false;
    private Canvas canvas = null;
    private GameLoop loop = null;

    Game(int w, int h, Canvas canvas, GameLoop loop) throws Exception {
        instance = this;

        String os = System.getProperty("os.name").toLowerCase();

        if(!os.startsWith("windows")) {
            reverseY = true;
        }

        assets = resources.manage(new AssetManager());

        this.loop = loop;

        if(canvas != null) {
            this.canvas = canvas;

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        gameLoop(null, false);
                    } catch(Exception ex) {
                        Log.put(0, ex);
                    }
                }

            });
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        } else {
            DisplayMode[] modes = Display.getAvailableDisplayModes();
            DisplayMode mode = new DisplayMode(w, h);
            boolean fs = false;
            Vector<Object> vModes = new Vector<>();

            for(DisplayMode iMode : modes) {
                if(iMode.isFullscreenCapable() && iMode.getBitsPerPixel() == 32) {
                    vModes.add(iMode);
                }
            }

            if(!vModes.isEmpty()) {
                Object[] aryModes = new Object[vModes.size()];

                for(int i = 0; i != aryModes.length; i++) {
                    aryModes[i] = vModes.get(i);
                }

                Object r = JOptionPane.showInputDialog(null, "Mode", "Mode", JOptionPane.QUESTION_MESSAGE, null, aryModes, aryModes[0]);

                if(r != null) {
                    fs = true;
                    mode = (DisplayMode)r;
                }
            }

            gameLoop(mode, fs);
        }
    }

    private void gameLoop(DisplayMode mode, boolean fs) throws Exception {
        try {
            if(mode != null) {
                Display.setDisplayMode(mode);
                Display.create();
            } else if(canvas != null) {
                Display.setParent(canvas);
                Display.create(
                    new PixelFormat()
                        .withBitsPerPixel(32)
                        .withAlphaBits(8)
                );
            } else {
                throw new Exception("invalid game loop parameters");
            }
            Display.makeCurrent();
            if(fs) {
                Display.setFullscreen(true);
            }
            Mouse.create();
            Keyboard.create();

            Log.put(0, "GL Version = '" + getGLVersion() + "'");

            loop.init();

            while(running) {
                Display.makeCurrent();

                GL11.glViewport(0, 0, w(), h());
                loop.render();
                GFX.checkError("display");
                Display.update(true);
                Display.sync(60);
                tick();

                if(keyDown(Keys.KEY_ESCAPE) && canvas == null) {
                    break;
                }
                if(Display.isCloseRequested()) {
                    break;
                }
            }
        } finally {
            destroy();
        }
    }

    public String getGLVersion() {
        return GL11.glGetString(GL11.GL_VERSION);
    }

    public AssetManager getAssets() {
        return assets;
    }

    @SuppressWarnings("unchecked")
    <T extends Renderer> T getRenderer(Class<? extends Renderer> cls) throws Exception {
        for(Renderer renderer : renderers) {
            if(cls.isAssignableFrom(renderer.getClass())) {
                return (T)renderer;
            }
        }

        Log.put(0, "creating renderer - " + cls.getName() + " ...");

        Renderer renderer = null;

        try {
            renderer = resources.manage((T)cls.getConstructors()[0].newInstance());
        } catch(InvocationTargetException ex) {
            Log.put(0, ex);
            throw ex;
        }

        renderers.add(renderer);

        return (T)renderer;
    }

    public SceneRenderer getSceneRenderer() throws Exception {
        return getRenderer(SceneRenderer.class);
    }

    public int mouseX() {
        return Mouse.getX();
    }

    public int mouseY() {
        int y = Mouse.getY();

        if(reverseY) {
            y = h() - y - 1;
        }
        return y;
    }

    public void setMouseGrabbed(boolean grabbed) {
        if(grabbed) {
            Mouse.setGrabbed(true);
            Mouse.poll();
            centerMouse();
        } else {
            Mouse.setGrabbed(false);
        }
    }

    public int dX() {
        return dX;
    }

    public int dY() {
        int y = dY;

        if(reverseY) {
            y = -y;
        }
        return y;
    }

    public boolean buttonDown(int i) {
        return Mouse.isButtonDown(i);
    }

    public boolean keyDown(int i) {
        return Keyboard.isKeyDown(i);
    }

    public int w() {
        return Display.getWidth();
    }

    public int h() {
        return Display.getHeight();
    }

    public float aspectRatio() {
        return w() / (float) h();
    }

    public float totalTime() {
        return (float) totalTime;
    }

    public float elapsedTime() {
        return (float) elapsedTime;
    }

    public int frameRate() {
        return fps;
    }

    public void resetTimer() {
        lastTime = System.nanoTime() / 1000000000.0;
        totalTime = 0;
        elapsedTime = 0;
        seconds = 0;
        frames = 0;
        fps = 0;
    }

    void addWindowListener(JFrame frame) {
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                running = false;
                frame.dispose();
            }
            
        });
    }

    private void destroy() {
        try {
            Log.put(0, Resource.getInstances() + " instance(s)");
            if(Display.isCreated()) {
                resources.destroy();
                Display.destroy();
            }
            if(Mouse.isCreated()) {
                Mouse.destroy();
            }
            if(Keyboard.isCreated()) {
                Keyboard.destroy();
            }
            Log.put(0, Resource.getInstances() + " instance(s)");
        } catch(Exception ex) {
            Log.put(0, ex);
        }
    }

    private void tick() {
        double nowTime = System.nanoTime() / 1000000000.0;
        elapsedTime = nowTime - lastTime;
        lastTime = nowTime;
        seconds += elapsedTime;
        totalTime += elapsedTime;
        frames++;
        if (seconds >= 1) {
            fps = frames;
            frames = 0;
            seconds = 0;
        }
        dX = Mouse.getDX();
        dY = Mouse.getDY();
        if(Mouse.isGrabbed()) {
            centerMouse();
        }
    }

    private void centerMouse() {
        Mouse.setCursorPosition(Display.getX() + Display.getWidth() / 2, Display.getY() + Display.getHeight() / 2);
    }
}
