package com.deeep.spaceglad.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.deeep.spaceglad.managers.SensWidget;


public class FireWidget extends Actor {
    private Image laser;
    public int time = 0;

    public FireWidget(){
        switch (SensWidget.color){
            case "pink":
                laser = new Image(new Texture("data/iaser_pink.png"));
                break;
            case "blue":
                laser = new Image(new Texture("data/laser_blue.png"));
                break;
            case "red":
                laser = new Image(new Texture("data/laser_red.png"));
                break;
        }
        laser.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (time > 0)
            laser.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        time--;
    }
}
