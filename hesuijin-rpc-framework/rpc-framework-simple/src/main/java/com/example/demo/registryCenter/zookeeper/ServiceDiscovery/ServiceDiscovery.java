package com.example.demo.registryCenter.zookeeper.ServiceDiscovery;

import com.example.common.extension.SPI;

import java.net.InetSocketAddress;

/**
 * @Description:服务发现类
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@SPI
public interface ServiceDiscovery {

    /**
     * 获取入参接口三要素下的全部子节点（Socket服务端的IP端口）
     * @param rpcServiceClassName 接口三要素
     * @return  Socket服务端的IP端口
     */
    InetSocketAddress lookupService(String rpcServiceClassName);
}
