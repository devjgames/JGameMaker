package org.game;

import javax.sound.sampled.*;

import java.io.*;

public final class Sound extends Resource {

    public final File file;

    private Clip clip;

    public Sound(File file) throws Exception {
        this.file = file;
        AudioInputStream stream = AudioSystem.getAudioInputStream(file);
        clip = AudioSystem.getClip();
        clip.open(stream);
        stream.close();
    }

    public void play(boolean looping) {
        if(!clip.isActive()) {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
            if(looping) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.loop(0);
            }
        }
    }

    public void stop() {
        clip.stop();
    }

    @Override
    void destroy() throws Exception {
        clip.stop();
        clip.close();
        super.destroy();
    }
}
