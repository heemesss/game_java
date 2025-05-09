package com.deeep.spaceglad.UI;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.deeep.spaceglad.Assets;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.Settings;
import com.deeep.spaceglad.screens.GameScreen;
import com.deeep.spaceglad.screens.MainMenuScreen;

public class PauseWidget extends Actor {
    private Core game;
    private Window window;
    private TextButton closeDialog, restartButton, quitButton, pauseButton;
    private Stage stage;

    public PauseWidget(Core game, Stage stage) {
        this.game = game;
        this.stage = stage;
        setWidgets();
        configureWidgets();
        setListeners();
    }

    private void setWidgets() {
        window = new Window("Pause", Assets.skin);
        closeDialog = new TextButton("X", Assets.skin);
        restartButton = new TextButton("Restart", Assets.skin);
        quitButton = new TextButton("Quit", Assets.skin);

        if (Gdx.app.getType() == Application.ApplicationType.Android)
            pauseButton = new TextButton("Pause", Assets.skin);
    }

    private void configureWidgets() {
        window.getTitleTable().add(closeDialog).height(window.getPadTop());
        window.add(restartButton);
        window.add(quitButton);
        if (Gdx.app.getType() == Application.ApplicationType.Android)
            stage.addActor(pauseButton);
    }

    private void setListeners() {
        super.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    handleUpdates();
                    return true;
                }
                return false;
            }
        });
        closeDialog.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                handleUpdates();
            }
        });
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent inputEvent, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        if (Gdx.app.getType() == Application.ApplicationType.Android)
            pauseButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent inputEvent, float x, float y) {
                    handleUpdates();
                }
            });
    }

    private void handleUpdates() {
        if (window.getStage() == null) {
            stage.addActor(window);
            Gdx.input.setCursorCatched(false);
            Settings.Paused = true;
            if (Gdx.app.getType() == Application.ApplicationType.Android)
                pauseButton.setVisible(false);
        } else {
            window.remove();
            Gdx.input.setCursorCatched(true);
            Settings.Paused = false;
            if (Gdx.app.getType() == Application.ApplicationType.Android)
                pauseButton.setVisible(true);
        }
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        window.setPosition(Gdx.graphics.getWidth() / 2f - window.getWidth() / 2, Gdx.graphics.getHeight() / 2f - window.getHeight() / 2);
        if (Gdx.app.getType() == Application.ApplicationType.Android)
            pauseButton.setPosition(Gdx.graphics.getWidth() - pauseButton.getWidth(), Gdx.graphics.getHeight() - pauseButton.getHeight());
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        restartButton.setWidth(restartButton.getWidth() * 2);
        quitButton.setWidth(quitButton.getWidth() * 2);
        window.setSize(restartButton.getWidth() + quitButton.getWidth(), height * 4);
        if (Gdx.app.getType() == Application.ApplicationType.Android)
            pauseButton.setSize(pauseButton.getWidth(), pauseButton.getHeight() * 2);
    }
}
