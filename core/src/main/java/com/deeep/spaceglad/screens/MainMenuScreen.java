package com.deeep.spaceglad.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.deeep.spaceglad.Assets;
import com.deeep.spaceglad.Core;

public class MainMenuScreen implements Screen {
    Core game;
    Stage stage;
    Image backgroundImage, titleImage;
    TextButton playButton, quitButton;

    public MainMenuScreen(Core game) {
        this.game = game;
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        setWidgets();
        configureWidgers();
        setListeners();

        Gdx.input.setInputProcessor(stage);
    }

    private void setWidgets() {
        backgroundImage = new Image(new Texture(Gdx.files.internal("data/backgroundMN.png")));
        titleImage = new Image(new Texture(Gdx.files.internal("data/title.png")));
        playButton = new TextButton("Play", Assets.skin);
        quitButton = new TextButton("Quit", Assets.skin);
    }

    private void configureWidgers() {
        backgroundImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        titleImage.setSize(Gdx.graphics.getWidth() / 3f, 200);
        titleImage.setPosition(Gdx.graphics.getWidth() / 6f * 2 - titleImage.getWidth() / 2, Gdx.graphics.getHeight() / 2f - titleImage.getHeight() / 2);
        playButton.setSize(256, 128);
        playButton.setPosition(Gdx.graphics.getWidth() / 4f * 3 - playButton.getWidth() / 2, Gdx.graphics.getHeight() / 2f + playButton.getHeight() * 0.5f);
        quitButton.setSize(256, 128);
        quitButton.setPosition(Gdx.graphics.getWidth() / 4f * 3 - playButton.getWidth() / 2, Gdx.graphics.getHeight() / 2f - quitButton.getHeight() * 1.5f);
        stage.addActor(backgroundImage);
        stage.addActor(titleImage);
        stage.addActor(playButton);
        stage.addActor(quitButton);
    }

    private void setListeners() {
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void show() {
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
}
