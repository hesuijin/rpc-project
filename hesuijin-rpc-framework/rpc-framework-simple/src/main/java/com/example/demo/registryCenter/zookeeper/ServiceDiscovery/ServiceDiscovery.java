package com.example.demo.registryCenter.zookeeper.ServiceDiscovery;

import java.net.InetSocketAddress;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
public interface ServiceDiscovery {

    InetSocketAddress lookupService(String rpcServiceName);
}