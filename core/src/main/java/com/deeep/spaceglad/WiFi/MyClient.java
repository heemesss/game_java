package com.deeep.spaceglad.WiFi;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Класс-клиент
 * отправляет запрос серверу и принимает от него ответ
 */

public class MyClient {
    Client client;

    public boolean isCantConnected;
    private final MyRequest request;
    private MyResponse response;
    private InetAddress address;

    public MyClient(MyRequest request) {
        this.request = request;
        response = new MyResponse();

        client = new Client();
        client.start();
        address = client.discoverHost(54778, 5000);
        try {
            client.connect(5000, address, 54556, 54778);
        } catch (Exception e) {
            isCantConnected = true; // если не удалось подключиться
            e.printStackTrace();
        }

        Kryo kryoClient = client.getKryo();
        kryoClient.register(MyRequest.class);
        kryoClient.register(MyResponse.class);

        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof MyResponse) {
                    response = (MyResponse) object;
                }
            }

            @Override
            public void connected(Connection connection) {
//                Listener.super.connected(connection);
            }

            @Override
            public void disconnected(Connection connection) {
//                Listener.super.disconnected(connection);
            }

            @Override
            public void idle(Connection connection) {
//                Listener.super.idle(connection);
            }
        });
    }

    public MyResponse getResponse() {
        return response;
    }

    public void send() {
        client.sendTCP(request);
    }

    public InetAddress getIp() {
        return address;
    }

    public void stop(){
        client.stop();
        client.close();
        try {
            client.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
