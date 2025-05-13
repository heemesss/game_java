package com.deeep.spaceglad.UI;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.deeep.spaceglad.Assets;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.managers.ControllerWidget;

public class GameUI {
    private Core game;
    public Stage stage;
    public HealthWidget healthWidget;
    private ScoreWidget scoreWidget;
    private PauseWidget pauseWidget;
    private CrosshairWidget crosshairWidget;
    public GameOverWidget gameOverWidget;
    private Label fpsLabel;
    private ControllerWidget controllerWidget;
    private StopwatchWidget stopwatchWidget;

    public GameUI(Core game) {
        this.game = game;
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        setWidgets();
        configureWidgets();
    }

    public void setWidgets() {
        healthWidget = new HealthWidget();
        scoreWidget = new ScoreWidget();
        pauseWidget = new PauseWidget(game, stage);
        gameOverWidget = new GameOverWidget(game, stage);
        crosshairWidget = new CrosshairWidget();
        fpsLabel = new Label("", Assets.skin);
        stopwatchWidget = new StopwatchWidget();
        if (Gdx.app.getType() == Application.ApplicationType.Android) controllerWidget = new ControllerWidget();
    }

    public void configureWidgets() {
        healthWidget.setSize(140, 25);
        healthWidget.setPosition(Gdx.graphics.getWidth() / 2f - healthWidget.getWidth() / 2, 0);
        scoreWidget.setSize(140, 25);
        scoreWidget.setPosition(0, Gdx.graphics.getHeight() - scoreWidget.getHeight());
        pauseWidget.setSize(64, 64);
        pauseWidget.setPosition(Gdx.graphics.getWidth() - pauseWidget.getWidth(), Gdx.graphics.getHeight() - pauseWidget.getHeight());
        gameOverWidget.setSize(280, 100);
        gameOverWidget.setPosition(Gdx.graphics.getWidth() / 2f - 140, Gdx.graphics.getHeight() / 2f);
        crosshairWidget.setPosition(Gdx.graphics.getWidth() / 2f - 16, Gdx.graphics.getHeight() / 2f - 16);
        crosshairWidget.setSize(32, 32);
        stopwatchWidget.setSize(140, 25);
        stopwatchWidget.setPosition(Gdx.graphics.getWidth() / 2f - healthWidget.getWidth() / 2, Gdx.graphics.getHeight() - stopwatchWidget.getHeight());

        fpsLabel.setPosition(0, 32);

        stage.addActor(healthWidget);
        stage.addActor(scoreWidget);
        stage.addActor(crosshairWidget);
        stage.addActor(stopwatchWidget);
        stage.setKeyboardFocus(pauseWidget);

        stage.addActor(fpsLabel);
        if (Gdx.app.getType() == Application.ApplicationType.Android) controllerWidget.addToStage(stage);
    }

    public void update(float delta) {
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
        stage.act(delta);
    }

    public void render() {
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void dispose() {
        stage.dispose();
    }
}
