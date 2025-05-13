package com.deeep.spaceglad.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.deeep.spaceglad.Assets;
import com.deeep.spaceglad.Settings;

public class HealthWidget extends Actor {
    private ProgressBar healthBar;
    private ProgressBar.ProgressBarStyle progressBarStyle;

    public HealthWidget() {
//        progressBarStyle = new ProgressBar.ProgressBarStyle(
//                Assets.skin.newDrawable("white", Color.RED),
//                Assets.skin.newDrawable("white", Color.GREEN));
        progressBarStyle = new ProgressBar.ProgressBarStyle(
            Assets.prog_bar,
            Assets.prog_bar_full);
        progressBarStyle.knobBefore = progressBarStyle.knob;
        healthBar = new ProgressBar(0, 100, 1, false, progressBarStyle);
        healthBar.setProgrammaticChangeEvents(true);
        healthBar.setAnimateDuration(0.1f);
        healthBar.setAnimateInterpolation(Interpolation.elastic);
    }

    @Override
    public void act(float delta) {
        if (Settings.Paused) return;
        healthBar.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        healthBar.draw(batch, parentAlpha);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        healthBar.setPosition(x, y);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        healthBar.setSize(width, height);
        progressBarStyle.background.setMinWidth(width);
        progressBarStyle.background.setMinHeight(height);
        progressBarStyle.knob.setMinWidth(healthBar.getValue());
        progressBarStyle.knob.setMinHeight(height);
    }

    public void setValue(float value) {
        healthBar.setValue(value);
    }
}
