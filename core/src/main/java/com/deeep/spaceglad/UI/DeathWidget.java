package com.deeep.spaceglad.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class DeathWidget extends Actor {
    public Image kill, death;

    private float time;

    public DeathWidget() {
        kill = new Image(new Texture("data/kill.png"));
        death = new Image(new Texture("data/death.png"));
    }

    public void setDis(){
        kill.setX(-kill.getWidth());
        death.setX(-death.getWidth());
    }

    public void setKill(){
        time = 0.5f;
        kill.setPosition(Gdx.graphics.getWidth() / 2f - kill.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - kill.getHeight() / 2f);
    }

    public void setDeath(){
        time = 1;
        death.setPosition(Gdx.graphics.getWidth() / 2f - death.getWidth() / 2f, Gdx.graphics.getHeight() / 2f - death.getHeight() / 2f);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        kill.setSize(width * 4, height * 4);
        death.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        setDis();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        kill.draw(batch, parentAlpha);
        death.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        if (time > 0)
            time -= delta;
        else {
            setDis();
        }
    }
}
