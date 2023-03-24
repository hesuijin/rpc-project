package com.example.demo.remotingCenter.transport.socket;

import com.example.common.factory.SingletonFactory;
import com.example.demo.remotingCenter.dto.RpcRequest;
import com.example.demo.remotingCenter.dto.RpcResponse;
import com.example.demo.remotingCenter.handler.RpcRequestHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/6
 */
@Slf4j
public class SocketRpcRequestHandlerRunnable implements Runnable {

    private final Socket socket;
    private final RpcRequestHandler rpcRequestHandler;


    public SocketRpcRequestHandlerRunnable(Socket socket) {
        this.socket = socket;
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void run() {
        //线程池中某个线程进行socket客户端请求的处理
        log.info("server handle message from client by thread: [{}]", Thread.currentThread().getName());
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {

            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            //根据从socket中读取的数据 形成请求  执行对应的方法
            Object result = rpcRequestHandler.handle(rpcRequest);
            //把结果写回去socket中
            objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("occur exception:", e);
        }
    }

}
