package com.deeep.spaceglad.WiFi;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.deeep.spaceglad.components.CharacterComponent;
import com.deeep.spaceglad.components.ModelComponent;

/**
 * Класс-ответ от сервера-клиенту
 */
public class MyResponse {
    public String text = "";
    public float x, y, z, qx, qy, qz, qw;
}
