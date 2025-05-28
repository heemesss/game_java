package com.deeep.spaceglad.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.GameWorld;
import com.deeep.spaceglad.GameWorldOnline;
import com.deeep.spaceglad.Settings;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.WiFi.MyClient;
import com.deeep.spaceglad.WiFi.MyRequest;
import com.deeep.spaceglad.WiFi.MyResponse;
import com.deeep.spaceglad.WiFi.MyServer;

public class OnlineGameScreen implements Screen {
    private Core game;
    private GameUI gameUI;
    private GameWorldOnline gameWorld;

    public OnlineGameScreen(Core game, MyServer server, MyClient client, MyRequest request,
                            MyResponse response){
        this.game = game;
        gameUI = new GameUI(game);
        gameWorld = new GameWorldOnline(gameUI, server, client, request, response);
        Settings.Paused = false;
        Gdx.input.setInputProcessor(gameUI.stage);
        Gdx.input.setCursorCatched(true);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        gameUI.update(delta);
        gameWorld.render(delta);
        gameUI.render();
    }

    @Override
    public void resize(int width, int height) {
        gameWorld.resize(width, height);
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
        gameWorld.dispose();
        gameUI.dispose();
    }
}
