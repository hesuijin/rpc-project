package com.example.demo.loadbalance;

import com.example.demo.loadbalance.LoadBalance;

import java.util.List;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String selectServiceAddress(List<String> serviceAddresses, String rpcServiceName) {
        if (serviceAddresses == null || serviceAddresses.size() == 0) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses, rpcServiceName);
    }

    /**
     *
     * @param serviceAddresses
     * @param rpcServiceName
     * @return
     */
    protected abstract String doSelect(List<String> serviceAddresses, String rpcServiceName);
}
