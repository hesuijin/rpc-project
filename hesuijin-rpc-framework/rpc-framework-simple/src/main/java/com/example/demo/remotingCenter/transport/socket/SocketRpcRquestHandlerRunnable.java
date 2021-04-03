//package com.example.demo.remotingCenter.transport.socket;
//
//import com.example.common.factory.SingletonFactory;
//import com.example.demo.remotingCenter.dto.RpcRequest;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.net.Socket;
//
///**
// * @Description:
// * @Author HeSuiJin
// * @Date 2021/4/4
// */
//@Slf4j
//public class SocketRpcRquestHandlerRunnable {
//
//    private final Socket socket;
//    private final RpcRequestHandler rpcRequestHandler;
//
//
//    public SocketRpcRequestHandlerRunnable(Socket socket) {
//        this.socket = socket;
//        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
//    }
//
//    @Override
//    public void run() {
//        log.info("server handle message from client by thread: [{}]", Thread.currentThread().getName());
//        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
//             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
//            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
//            Object result = rpcRequestHandler.handle(rpcRequest);
//            objectOutputStream.writeObject(RpcResponse.success(result, rpcRequest.getRequestId()));
//            objectOutputStream.flush();
//        } catch (IOException | ClassNotFoundException e) {
//            log.error("occur exception:", e);
//        }
//    }
//}
