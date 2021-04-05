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
        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test2").version("version2").build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceProperties);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello = helloService.helloServiceApi(new Hello("111", "222"));
        System.out.println(hello);
    }

}
