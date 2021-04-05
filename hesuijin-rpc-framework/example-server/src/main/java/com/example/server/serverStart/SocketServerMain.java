package com.example.server.serverStart;

import com.example.api.HelloService;
import com.example.common.entity.RpcServiceProperties;
import com.example.demo.remotingCenter.transport.socket.SocketRpcServer;
import com.example.server.serviceImpl.HelloServiceImpl1;

/**
 * @Description:
 * 启动服务
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
public class SocketServerMain {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl1();
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();

        socketRpcServer.registerService(helloService, rpcServiceProperties);

        //启动socketRpcServer 启动服务端
        socketRpcServer.start();
    }

}
