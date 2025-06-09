package com.deeep.spaceglad.WiFi;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.deeep.spaceglad.components.CharacterComponent;
import com.deeep.spaceglad.components.ModelComponent;

/**
 * Класс-запрос от клиента-серверу
 */
public class MyRequest {
    public String text = "";
    public float x, y, z, rotate;
    public int score = 0;
}
