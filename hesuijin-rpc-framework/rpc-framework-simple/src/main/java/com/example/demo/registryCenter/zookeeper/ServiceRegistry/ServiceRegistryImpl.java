package com.example.demo.registryCenter.zookeeper.ServiceRegistry;

import java.net.InetSocketAddress;

/**
 * @Description:
 * 创建持久化节点
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
public class ServiceRegistryImpl implements ServiceRegistry {

    /**用于创建持久化节点
     * @param rpcServiceName
     * @param inetSocketAddress
     */
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
    }
}
