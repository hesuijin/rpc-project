package com.example.demo.loadbalance;

import com.example.common.extension.SPI;

import java.util.List;

/**
 * @Description:
 * 负载均衡 获取ServiceAddress策略
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@SPI
public interface LoadBalance {

    String selectServiceAddress(List<String> serviceAddresses, String rpcServiceName);
}
