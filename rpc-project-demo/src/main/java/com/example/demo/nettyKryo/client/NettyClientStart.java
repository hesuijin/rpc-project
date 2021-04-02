package com.example.demo.nettyKryo.client;

import com.example.demo.nettyKryo.dto.RpcRequest;
import com.example.demo.nettyKryo.dto.RpcResponse;

/**
 * @Author HeSuiJin
 * @Date 2021/3/21 22:22
 * @Description:
 */
public class NettyClientStart {

    //客户端启动
    public static void main(String[] args) {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName("interface")
                .methodName("你好啊").build();
        //
        NettyClient nettyClient = new NettyClient("127.0.0.1", 8888);
        for (int i = 0; i < 3; i++) {
            nettyClient.sendMessage(rpcRequest);
        }
        RpcResponse rpcResponse = nettyClient.sendMessage(rpcRequest);
        System.out.println(rpcResponse.toString());
    }
}
