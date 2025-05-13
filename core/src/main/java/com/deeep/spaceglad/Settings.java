package com.deeep.spaceglad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {
    public static boolean Paused;
    public static int[] highscores = new int[]{0, 0, 0, 0, 0};
    public final static String file = ".spaceglad";

    public static void load() {
        try {
            FileHandle filehandle = Gdx.files.external(file);
            String[] strings = filehandle.readString().split("\n");
            for (int i = 0; i < 5; i++) highscores[i] = Integer.parseInt(strings[i]);
        } catch (Throwable e) {
        }
    }

    @SuppressWarnings("NewApi")
    public static void save() {
        try {
            FileHandle filehandle = Gdx.files.external(file);
            Files.writeString(Paths.get(filehandle.path()), "");
            for (int i = 0; i < 5; i++) filehandle.writeString(highscores[i] + "\n", true);
        } catch (Throwable e) {
        }
    }

    public static void sendScore(int score) {
        for (int i = 0; i < 5; i++) {
            if (highscores[i] < score){
                for (int j = 4; j > i; j--) {
                    highscores[j] = highscores[j - 1];
                }
                highscores[i] = score;
                break;
            }
        }
    }
}
