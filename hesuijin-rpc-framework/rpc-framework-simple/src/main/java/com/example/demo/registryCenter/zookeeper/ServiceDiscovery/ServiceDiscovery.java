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
     * @param rpcServiceClassName 包含className（interfaceName）服务接口名称 com.example.demo.HelloService
     * @return
     */
    InetSocketAddress lookupService(String rpcServiceClassName);
}
