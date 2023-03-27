package com.example.client.clientStart;

import com.example.api.HelloService;
import com.example.common.entity.RpcServiceProperties;
import com.example.demo.annotation.RpcScan;
import com.example.demo.proxy.RpcClientProxy;
import com.example.demo.remotingCenter.transport.RpcRequestTransport;
import com.example.demo.remotingCenter.transport.netty.client.NettyRpcClient;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * Netty客户端发起调用
 */
@RpcScan(basePackage = {"com.example.client.clientStart"})
//@RpcScan(basePackage = {"com.example"})
public class NettyClientMain {

    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");

        //可以通过手动获取代理对象
//        RpcRequestTransport rpcRequestTransport = new NettyRpcClient();
//        RpcServiceProperties rpcServicePropertiesOther = RpcServiceProperties.builder()
//                .group("nettyGroup").version("nettyVersion").build();
//        RpcClientProxy rpcClientProxy= new RpcClientProxy(rpcRequestTransport, rpcServicePropertiesOther);
//        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
//        HelloController helloController = new HelloController(helloService);

        helloController.test();
    }

}


