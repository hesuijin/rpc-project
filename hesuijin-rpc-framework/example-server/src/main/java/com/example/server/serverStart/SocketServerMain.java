package com.example.server.serverStart;

import com.example.api.HelloService;
import com.example.common.entity.RpcServiceProperties;
import com.example.demo.remotingCenter.transport.socket.SocketRpcServer;
import com.example.server.serviceImpl.HelloServiceImpl;
import com.example.server.serviceImpl.HelloServiceImplOther;

/**
 * @Description:
 * 1：创建一个 SocketRpcServer 的服务端
 * 2：注册接口相关信息到 zk中
 * 3：启动 SocketRpcServer 服务端
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
public class SocketServerMain {

    public static void main(String[] args) {
        //1： 创建一个 SocketRpcServer 的服务端
        SocketRpcServer socketRpcServer = new SocketRpcServer();
        //2 ：注册接口相关信息到 zk中
        registerService(socketRpcServer);
        //3： 启动 SocketRpcServer 服务端
        socketRpcServer.start();
    }

    /**
     * 进行注册
     * @param socketRpcServer
     */
    private static  void  registerService(SocketRpcServer socketRpcServer  ){
        //设置需要注册的接口的  group 和 version
        //维度为 接口信息(接口位置 接口类名) - 接口所在组 -接口版本号    其中接口所在组与接口版本号可以随意修改
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("socketSeverNameGroup").version("socketSeverNameVersion").build();

        //创建一个需要注册的接口实现类对象 （后续通过该类的对象拿 该对象的接口信息）
        HelloService helloService = new HelloServiceImpl();
        //注册接口相关信息到 zk中 的详细逻辑
        socketRpcServer.registerService(helloService, rpcServiceProperties);

        RpcServiceProperties rpcServicePropertiesOther = RpcServiceProperties.builder()
                .group("socketSeverNameGroupOther").version("socketSeverNameVersionOther").build();

        //创建一个需要注册的接口实现类对象 （后续通过该类的对象拿 该对象的接口信息）
        HelloService helloServiceOther = new HelloServiceImplOther();
        //注册接口相关信息到 zk中 的详细逻辑
        socketRpcServer.registerService(helloServiceOther, rpcServicePropertiesOther);
    }

}
