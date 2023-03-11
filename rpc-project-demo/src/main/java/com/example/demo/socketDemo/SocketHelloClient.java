package com.example.demo.socketDemo;

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
        //try括里面的Socket 当结束后 会关闭Socket
        // 1：创建客户端socket
        try (Socket socket = new Socket(host, port)) {
            // 2：客户端socket获取 输入 输出流
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(socketMessage);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            // 3：发送给服务端的信息 或者 获取服务端返回的信息
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