package com.deeep.spaceglad.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.deeep.spaceglad.Assets;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.GameWorld;
import com.deeep.spaceglad.UI.GameUI;
import com.deeep.spaceglad.WiFi.MyClient;
import com.deeep.spaceglad.WiFi.MyRequest;
import com.deeep.spaceglad.WiFi.MyResponse;
import com.deeep.spaceglad.WiFi.MyServer;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Objects;

public class OnlineGameScreen implements Screen {
    Core game;
    Stage stage;
    TextButton hostButton, clientButton, startButton;
    Label label;

    boolean isEnterIP;

    private InetAddress ipAddress;
    private String ipAddressOfServer = "?";
    MyServer server;
    MyClient client;
    boolean isServer;
    boolean isClient;
    MyRequest requestFromClient;
    MyResponse responseFromServer;

    public OnlineGameScreen(Core game){
        this.game = game;

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        setWidgets();
        configureWidgers();
        setListeners();

        Gdx.input.setInputProcessor(stage);

        requestFromClient = new MyRequest();
        responseFromServer = new MyResponse();
    }

    private void setWidgets() {
        hostButton = new TextButton("I am Server", Assets.skin);
        clientButton = new TextButton("I am Client", Assets.skin);
        startButton = new TextButton("Start", Assets.skin);
        label = new Label(ipAddressOfServer, Assets.skin);
    }

    private void configureWidgers() {
        hostButton.setSize(256, 128);
        hostButton.setPosition(Gdx.graphics.getWidth() / 4f * 3 - hostButton.getWidth() / 2, Gdx.graphics.getHeight() / 2f + hostButton.getHeight());
        clientButton.setSize(256, 128);
        clientButton.setPosition(Gdx.graphics.getWidth() / 4f * 3 - clientButton.getWidth() / 2, Gdx.graphics.getHeight() / 2f - clientButton.getHeight() / 2);
        startButton.setSize(256, 128);
        startButton.setPosition(Gdx.graphics.getWidth() / 4f - startButton.getWidth() / 2, Gdx.graphics.getHeight() / 2f + startButton.getHeight());
        startButton.setDisabled(false);
        startButton.setVisible(false);

        label.setSize(256, 128);

        stage.addActor(hostButton);
        stage.addActor(clientButton);
        stage.addActor(label);
        stage.addActor(startButton);
    }

    private void setListeners() {
        hostButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isServer && !isClient && !isEnterIP){
                    server = new MyServer(responseFromServer);
                    isServer = true;
                    ipAddressOfServer = detectIP();
                    label.setText(ipAddressOfServer);
                }
            }
        });

        clientButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isServer && !isClient && !isEnterIP){
                    isClient = true;
                    client = new MyClient(requestFromClient);
                    ipAddressOfServer = client.getIp().getHostAddress();
                    label.setText(ipAddressOfServer);
                    requestFromClient.text = "Connect";
                    client.send();
                    if (client.isCantConnected){
                        isClient = false;
                        client = null;
                        ipAddressOfServer = "Server not found";
                    }
                }
            }
        });

        startButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                responseFromServer.text = "Start";
                game.setScreen(new GameScreen(game));
            }
        });
    }

    public String detectIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLinkLocalAddress() && !address.isLoopbackAddress() && address.getHostAddress().indexOf(":") == -1) {
                        ipAddress = address;
                        //System.out.println("IP-адрес устройства: " + ipAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        if(ipAddress != null){
            return ipAddress.getHostAddress();
        }
        return "";
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        if (isClient)
            client.send();
        if (isServer && Objects.equals(server.getRequest().text, "Connect")){
            startButton.setDisabled(true);
            startButton.setVisible(true);
        }
        if (isClient && Objects.equals(client.getResponse().text, "Start")){
            game.setScreen(new GameScreen(game));
        }
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
