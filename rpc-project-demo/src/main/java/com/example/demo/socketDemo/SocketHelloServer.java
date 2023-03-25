package com.example.demo.socketDemo;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * socketDemo 服务端
 * @author HSJ
 */
@Slf4j
public class SocketHelloServer {

//    try-with-resource语句:
//        try括号中包含资源声明了一种或多种资源，并且保证每个声明了的资源在语句结束时都会被自动关闭，
//        那么如何定义资源，任何实现了java.lang.AutoCloseable（Closeable）接口的对象，都可以在try-with-resource语句中使用，
//        最终在语句结束时会把资源全部关闭掉。
//
//        如： try (Socket socket = new Socket(host, port))   中最后会自动执行 socket.close();
//        输出流 输入流也是类似：
//        objectOutputStream.close();  objectInputStream.close();
//
//    objectOutputStream.flush():
//        部分outputstream的子类实现了缓存机制，为了提高效率,当write()的时候不一定直接发过去，有可能先缓存起来一起发出去,
//        flush()的作用就是强制性地将缓存中的数据发出去.

    public void start(int port) {
        //1.创建 ServerSocket 对象并且绑定一个端口  当结束后 会关闭ServerSocket
        try (ServerSocket server = new ServerSocket(port);) {
            Socket socket;
            //2.通过 accept()方法监听客户端请求
            while ((socket = server.accept()) != null) {
                log.info("client connected");
                   //try括里面的Stream 当结束后 会自动关闭流
                try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                     ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                   //3.通过输入流读取客户端发送的请求信息
                    SocketMessage socketMessage = (SocketMessage) objectInputStream.readObject();
                    log.info("server receive message:" + socketMessage.getContent());
                    socketMessage.setContent("new content");
                    //4.通过输出流向客户端发送响应信息
                    objectOutputStream.writeObject(socketMessage);
                    objectOutputStream.flush();
                } catch (IOException | ClassNotFoundException e) {
                    log.error("occur exception:", e);
                }
            }
        } catch (IOException e) {
            log.error("occur IOException:", e);
        }
    }

    public static void main(String[] args) {
        SocketHelloServer socketHelloServer = new SocketHelloServer();
        socketHelloServer.start(6666);
    }
}

//        new Thread(() -> {
//            // 创建 socketDemo 连接
//        }).start();
//
//        ThreadFactory threadFactory = Executors.defaultThreadFactory();
//        ExecutorService threadPool = new ThreadPoolExecutor(10, 100, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100), threadFactory);
//        threadPool.execute(() -> {
//            // 创建 socketDemo 连接
//        });