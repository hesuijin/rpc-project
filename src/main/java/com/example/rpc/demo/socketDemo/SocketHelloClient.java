package com.example.rpc.demo.socketDemo;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * socket客户端
 * @author HSJ
 */
@Slf4j
public class SocketHelloClient {


    public Object send(SocketMessage socketMessage, String host, int port) {
        //try括里面的socket 当结束后 会自动socket
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(socketMessage);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur exception:", e);
        }
        return null;
    }

    public static void main(String[] args) {
        SocketHelloClient socketHelloClient = new SocketHelloClient();
        SocketMessage socketMessage = (SocketMessage) socketHelloClient.send(new SocketMessage("content from client"), "127.0.0.1", 6666);
        System.out.println("client receive message:" + socketMessage.getContent());
    }
}