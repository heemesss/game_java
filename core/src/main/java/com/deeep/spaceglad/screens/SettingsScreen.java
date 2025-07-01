package com.deeep.spaceglad.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.deeep.spaceglad.Assets;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.managers.SensWidget;

import java.util.Objects;

public class SettingsScreen implements Screen {
    Core game;
    Stage stage;
    Image backgroundImage;
    Label sens, control;
    SelectBox<String> selectBox, selectColor;
    SelectBox<Float> selectSens;
    TextButton backButton;

    public SettingsScreen(Core game){
        this.game = game;
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        setWidgets();
        configureWidgets();
        setListeners();
        Gdx.input.setInputProcessor(stage);
    }

    private void setWidgets(){
        backgroundImage = new Image(new Texture(Gdx.files.internal("data/backgroundMN.png")));
        sens = new Label("sensitive", Assets.skin);
        control = new Label("control mode", Assets.skin);
        selectBox = new SelectBox<>(Assets.skin);
        selectSens = new SelectBox<>(Assets.skin);
        selectColor = new SelectBox<>(Assets.skin);
        selectSens.setItems(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f,
            1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 2.0f);
        selectSens.setSelected(SensWidget.sens);
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            selectBox.setItems("normal", "accelerometer");
        }
        else {
            selectBox.setItems("normal");
        }

        selectColor.setItems("blue", "pink", "red");
        selectColor.setSelected(SensWidget.color);
        selectBox.setSelected(SensWidget.mode ? "normal" : "accelerometer");
        backButton = new TextButton("Back", Assets.skin);
    }

    private void configureWidgets(){
        backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        sens.setPosition(800 - sens.getWidth(), 510); // text
        control.setPosition(800 - control.getWidth(), 310); // text

        selectSens.setPosition(800, 500);
        selectSens.setWidth(256);

        selectBox.setPosition(800, 300);
        selectBox.setWidth(256);

        selectColor.setPosition(800, 100);
        selectColor.setWidth(256);

        backButton.setSize(256, 128); // button back
        backButton.setPosition(0, Gdx.graphics.getHeight() - 128);


        stage.addActor(backgroundImage);
        stage.addActor(selectSens);
        stage.addActor(sens);
        stage.addActor(selectBox);
        stage.addActor(control);
        stage.addActor(backButton);
        stage.addActor(selectColor);
    }

    private void setListeners(){
        selectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SensWidget.mode = Objects.equals(selectBox.getSelected(), "normal");
            }
        });

        selectColor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SensWidget.color = selectColor.getSelected();
            }
        });

        selectColor.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SensWidget.sens = (int)(selectSens.getSelected() * 10) / 10f;
            }
        });

        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();

        System.out.println(SensWidget.color);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
