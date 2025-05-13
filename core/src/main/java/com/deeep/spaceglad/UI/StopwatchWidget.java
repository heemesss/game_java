package com.deeep.spaceglad.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.deeep.spaceglad.Assets;
import com.deeep.spaceglad.Settings;

public class StopwatchWidget extends Actor {
    private float time = 0;
    private Label label;

    StopwatchWidget(){
        label = new Label("", Assets.skin);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        label.setText((int)time / 60 + ":" + (int)time % 60);
        label.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        if (!Settings.Paused)
            time += delta;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        label.setPosition(x, y);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        label.setSize(width, height);
    }
}
