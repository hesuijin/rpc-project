package com.example.demo.loadbalance.abstractLoadBalance;

import com.example.demo.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

/**
 * 负载均衡策略  随机策略
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    /**
     * 重写了 AbstractLoadBalance的doSelect方法
     * @param serviceAddresses
     * @param rpcServiceName
     * @return
     */
    @Override
    protected String doSelect(List<String> serviceAddresses, String rpcServiceName) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
