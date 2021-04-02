package com.example.demo.registry.zookeeper.ServiceRegistry;

import java.net.InetSocketAddress;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
public interface ServiceRegistry {

    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}