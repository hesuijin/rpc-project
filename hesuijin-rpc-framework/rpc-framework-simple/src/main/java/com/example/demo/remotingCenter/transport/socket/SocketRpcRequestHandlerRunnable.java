package com.example.demo.remotingCenter.transport.socket;

import com.example.common.factory.SingletonFactory;
import com.example.demo.remotingCenter.dto.RpcRequest;
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



    public SocketRpcRequestHandlerRunnable(Socket socket) {
        this.socket = socket;
//        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void run() {
//
    }

}
