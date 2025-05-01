package com.deeep.spaceglad.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.deeep.spaceglad.Assets;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.Settings;
import com.deeep.spaceglad.components.PlayerComponent;
import com.deeep.spaceglad.screens.GameScreen;
import com.deeep.spaceglad.screens.MainMenuScreen;

public class GameOverWidget extends Actor {
    private Core game;
    private Stage stage;
    private Image image;
    private TextButton retryB, quitB;

    public GameOverWidget(Core game, Stage stage) {
        this.game = game;
        this.stage = stage;
        setWidgets();
        setListeners();
    }

    private void setWidgets() {
        image = new Image(new Texture(Gdx.files.internal("data/gameOver.png")));
        retryB = new TextButton("Retry", Assets.skin);
        quitB = new TextButton("Quit", Assets.skin);
    }

    private void setListeners() {
        retryB.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });
        quitB.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(0, 0);
        image.setPosition(x, y + 32);
        retryB.setPosition(x + image.getWidth() / 2 - retryB.getWidth() / 4 * 5, y - 96);
        quitB.setPosition(x + image.getWidth() / 2 + retryB.getWidth() / 4, y - 96);
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
        image.setSize(width, height);
        retryB.setSize(width / 2.5f, height / 2);
        quitB.setSize(width / 2.5f, height / 2);
    }

    public void gameOver() {
        stage.addActor(image);
        stage.addActor(retryB);
        stage.addActor(quitB);
        stage.unfocus(stage.getKeyboardFocus());
        Gdx.input.setCursorCatched(false);
        Settings.sendScore(PlayerComponent.score);
    }
}
