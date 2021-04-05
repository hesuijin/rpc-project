package com.example.server.serverStart;

import com.example.api.HelloService;
import com.example.common.entity.RpcServiceProperties;
import com.example.demo.remotingCenter.transport.socket.SocketRpcServer;
import com.example.server.serviceImpl.HelloServiceImpl1;

/**
 * @Description:
 * 1:创建一个 SocketRpcServer 的服务端
 * 2:注册接口到 zk
 * 3：启动 SocketRpcServer 服务端
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
public class SocketServerMain {
    public static void main(String[] args) {

//        socketRpcServer 不再是单纯的socket服务端 而是 Rpc框架 socket的服务端

        //1：创建一个 SocketRpcServer 的服务端
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        //2 ：注册接口到 zk中
        registerService(socketRpcServer);
        //3： 启动 SocketRpcServer 服务端
        socketRpcServer.start();
    }

    private static  void  registerService(SocketRpcServer socketRpcServer  ){
        //创建一个需要注册的接口实现类对象 （后续通过该类对象拿 该对象的接口）
        HelloService helloService = new HelloServiceImpl1();
        //设置需要注册的接口的  group 和 version
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        //注册接口到 zk中 的详细逻辑
        socketRpcServer.registerService(helloService, rpcServiceProperties);
    }

}
