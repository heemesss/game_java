package com.deeep.spaceglad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Settings {
    public static boolean Paused;
    public static int[] highscores = new int[]{0, 0, 0, 0, 0};

    public static void load() {
        Preferences preferences = Gdx.app.getPreferences("pref");

        for (int i = 0; i < 5; i++)
            highscores[i] = preferences.getInteger("" + i);
    }

    public static void save() {
        Preferences preferences = Gdx.app.getPreferences("pref");

        for (int i = 0; i < 5; i++)
            preferences.putInteger("" + i, highscores[i]);

        preferences.flush();
        System.out.println(Arrays.toString(highscores));
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
