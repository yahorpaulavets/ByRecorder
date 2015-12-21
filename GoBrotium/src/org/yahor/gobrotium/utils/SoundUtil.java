package org.yahor.gobrotium.utils;

import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class SoundUtil {
    private static Player player;

    public static void close() { if (player != null) player.close(); }

    // playMp3File the MP3 file to the sound card
    public static void playMp3File(String filename) {
        try {
            FileInputStream fis     = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);

        }
        catch (Exception e) {
            L.d("Problem playing file " + filename);
        }
        try { player.play(); }
        catch (Exception e) { L.d(e.toString()); }

        close();
    }
}
