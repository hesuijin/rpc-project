package com.example.rpc.remoting.transport.netty.kyro.server;

/**
 * Netty 服务端启动类
 * @Author HeSuiJin
 * @Date 2021/3/21 22:22
 * @Description:
 */
public class NettyServerStart {
    //服务端启动
    public static void main(String[] args) {
        new NettyServer(8889).run();
    }
}
