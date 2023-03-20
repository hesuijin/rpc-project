package com.example.client.clientStart;

import com.example.api.Hello;
import com.example.api.HelloService;
import com.example.common.entity.RpcServiceProperties;
import com.example.demo.proxy.RpcClientProxy;
import com.example.demo.remotingCenter.transport.RpcRequestTransport;
import com.example.demo.remotingCenter.transport.socket.SocketRpcClient;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
public class SocketClientMain {

    public static void main(String[] args) {

        //获取ServiceDiscoveryImpl实现类  单例的调用方
        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();

        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("socketSeverNameGroup").version("socketSeverNameVersion").build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceProperties);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.helloServiceApi(new Hello("测试socket发送信息", "测试socket"));
        System.out.println(hello);

        RpcServiceProperties rpcServicePropertiesOther = RpcServiceProperties.builder()
                .group("socketSeverNameGroupOther").version("socketSeverNameVersionOther").build();
        RpcClientProxy rpcClientProxyOhther = new RpcClientProxy(rpcRequestTransport, rpcServicePropertiesOther);
        HelloService helloServiceOther = rpcClientProxyOhther.getProxy(HelloService.class);
        String helloOther = helloServiceOther.helloServiceApi(new Hello("测试socket发送信息 Other", "测试socket Other"));
        System.out.println(helloOther);
    }

}
