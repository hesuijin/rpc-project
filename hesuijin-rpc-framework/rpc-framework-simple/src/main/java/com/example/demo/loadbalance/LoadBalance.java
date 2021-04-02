package com.example.demo.loadbalance;

import java.util.List;

/**
 * @Description:
 * 负载均衡 获取ServiceAddress策略
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
public interface LoadBalance {

    String selectServiceAddress(List<String> serviceAddresses, String rpcServiceName);
}
