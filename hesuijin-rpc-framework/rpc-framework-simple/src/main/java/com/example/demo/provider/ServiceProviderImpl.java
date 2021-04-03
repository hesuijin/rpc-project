package com.example.demo.provider;

import com.example.common.entity.RpcServiceProperties;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/4
 */
public class ServiceProviderImpl implements ServiceProvider{
    @Override
    public void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties) {

    }

    @Override
    public Object getService(RpcServiceProperties rpcServiceProperties) {
        return null;
    }

    @Override
    public void publishService(Object service, RpcServiceProperties rpcServiceProperties) {

    }

    @Override
    public void publishService(Object service) {

    }
}
