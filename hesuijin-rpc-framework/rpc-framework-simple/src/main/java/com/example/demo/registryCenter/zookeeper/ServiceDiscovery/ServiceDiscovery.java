package com.example.demo.registryCenter.zookeeper.ServiceDiscovery;

import java.net.InetSocketAddress;

/**
 * @Description:服务发现
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
public interface ServiceDiscovery {

    /**
     * 获取远程服务地址
     * @param rpcServiceName 完整的服务名称（class name+group+version）
     * @return
     */
    InetSocketAddress lookupService(String rpcServiceName);
}
