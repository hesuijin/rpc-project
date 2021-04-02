package com.example.demo.registryCenter.zookeeper.ServiceRegistry;

import java.net.InetSocketAddress;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
public interface ServiceRegistry {

    /**
     * @param rpcServiceName
     * @param inetSocketAddress
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);
}
