package com.deeep.spaceglad.WiFi;

import com.deeep.spaceglad.WiFi.MyRequest;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

/**
 * Класс - сервер
 * принимает запрос от клиента и отправляет ему ответ
 */

public class MyServer {
    Server server;
    private MyRequest request;

    public MyServer(final MyResponse response) {
        request = new MyRequest();
        server = new Server();
        server.start();
        try {
            server.bind(54556, 54778);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Kryo kryo = server.getKryo();
        kryo.register(MyRequest.class);
        kryo.register(MyResponse.class);

        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
//                Listener.super.received(connection, object);
                if (object instanceof MyRequest) {
                    request = (MyRequest) object;
                    connection.sendTCP(response);
                }
            }

            @Override
            public void connected(Connection connection) {
//                Listener.super.connected(connection);
                connection.isConnected();
            }

            @Override
            public void disconnected(Connection connection) {
                Listener.super.disconnected(connection);
                connection.close();
            }

            @Override
            public void idle(Connection connection) {
//                Listener.super.idle(connection);
                connection.isIdle();
            }
        });
    }

    public MyRequest getRequest() {
        return request;
    }
    public void stop(){

        try {
            server.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
