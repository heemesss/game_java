package com.deeep.spaceglad.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.deeep.spaceglad.Assets;
import com.deeep.spaceglad.Core;
import com.deeep.spaceglad.WiFi.MyClient;
import com.deeep.spaceglad.WiFi.MyRequest;
import com.deeep.spaceglad.WiFi.MyResponse;
import com.deeep.spaceglad.WiFi.MyServer;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Objects;

public class OnlineMenuScreen implements Screen {
    private Core game;
    private Stage stage;
    private ImageTextButton hostButton, clientButton, startButton, backButton;
    private Label label, labelMessage;
    private Image background;

    boolean isEnterIP;

    private InetAddress ipAddress;
    private String ipAddressOfServer = "?";
    private MyServer server;
    private MyClient client;
    boolean isServer;
    boolean isClient;
    private MyRequest requestFromClient;
    private MyResponse responseFromServer;

    public OnlineMenuScreen(Core game){
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
        background = new Image(new Texture(Gdx.files.internal("data/backgroundMN.png")));
        hostButton = new ImageTextButton("Create", Assets.style);
        clientButton = new ImageTextButton("Join", Assets.style);
        startButton = new ImageTextButton("Start", Assets.style);
        backButton = new ImageTextButton("Back", Assets.style);
        label = new Label(ipAddressOfServer, Assets.skin);
        labelMessage = new Label("Create or Join", Assets.skin);
    }

    private void configureWidgers() {
        hostButton.setSize(256, 128);
        hostButton.setPosition(Gdx.graphics.getWidth() / 4f * 3 - hostButton.getWidth() / 2, Gdx.graphics.getHeight() / 2f + hostButton.getHeight());
        clientButton.setSize(256, 128);
        clientButton.setPosition(Gdx.graphics.getWidth() / 4f * 3 - clientButton.getWidth() / 2, Gdx.graphics.getHeight() / 2f - clientButton.getHeight() / 2);
        startButton.setSize(256, 128);
        startButton.setPosition(Gdx.graphics.getWidth() / 4f - startButton.getWidth() / 2, Gdx.graphics.getHeight() / 2f - startButton.getHeight());
        backButton.setSize(256, 128);
        backButton.setPosition(0, Gdx.graphics.getHeight() - backButton.getHeight());
        startButton.setDisabled(false);
        startButton.setVisible(false);

        label.setSize(256, 128);
        labelMessage.setSize(256, 128);
        labelMessage.setPosition(Gdx.graphics.getWidth() / 2f - labelMessage.getWidth(), labelMessage.getHeight() / 2f);

        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage.addActor(background);
        stage.addActor(hostButton);
        stage.addActor(clientButton);
        stage.addActor(label);
        stage.addActor(startButton);
        stage.addActor(labelMessage);
        stage.addActor(backButton);
    }

    private void setListeners() {
        hostButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isServer && !isClient && !isEnterIP){
                    labelMessage.setText("Creating...");
                    server = new MyServer(responseFromServer);
                    isServer = true;
                    ipAddressOfServer = detectIP();
                    label.setText(ipAddressOfServer);
                    labelMessage.setText("Ask someone to connect");
                }
            }
        });

        clientButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isServer && !isClient && !isEnterIP){
                    labelMessage.setText("Connecting...");
                    isClient = true;
                    client = new MyClient(requestFromClient);
                    try {
                        ipAddressOfServer = client.getIp().getHostAddress();
                        label.setText(ipAddressOfServer);
                        requestFromClient.text = "Connect";
                        client.send();
                        labelMessage.setText("Connect");
                    } catch (Exception e) {
                        labelMessage.setText("Error!");
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
                game.setScreen(new OnlineGameScreen(game, server, client, requestFromClient, responseFromServer));
            }
        });

        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
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
            game.setScreen(new OnlineGameScreen(game, server, client, requestFromClient, responseFromServer));
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
