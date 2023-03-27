package com.example.server.serverStart;

import com.example.api.HelloService;
import com.example.common.entity.RpcServiceProperties;
import com.example.demo.annotation.RpcScan;
import com.example.demo.remotingCenter.transport.netty.server.NettyRpcServer;
import com.example.server.serviceImpl.HelloServiceNettyImpl;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * Netty服务端启动
 */
@RpcScan(basePackage = {"com.example.demo.remotingCenter.transport.netty","com.example.server.serviceImpl"})
//@RpcScan(basePackage = {"com.example"})
public class NettyServerMain {

    public static void main(String[] args) {
        // Register service via annotation
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerMain.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) applicationContext.getBean("nettyRpcServer");

        //可以通过手动注册接口三要素
//        HelloService helloService = new HelloServiceNettyImpl();
//        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
//                .group("nettyGroup").version("nettyVersion").build();
//        nettyRpcServer.registerService(helloService, rpcServiceProperties);

        nettyRpcServer.start();
    }
}
