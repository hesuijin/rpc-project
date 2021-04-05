package com.example.demo.provider;

import com.example.common.entity.RpcServiceProperties;

/**
 * @Description:
 *  存储 和 提供 服务对象
 * @Author HeSuiJin
 * @Date 2021/4/3
 */
public interface ServiceProvider {

    /**
     * @param service              service object
     * @param serviceClass         the interface class implemented by the service instance object
     * @param rpcServiceProperties service related attributes
     */
    void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties);

    /**
     * @param rpcServiceProperties service related attributes
     * @return service object
     */
    Object getService(RpcServiceProperties rpcServiceProperties);

    /**
     * @param service              service object
     * @param rpcServiceProperties service related attributes
     */
    void publishService(Object service, RpcServiceProperties rpcServiceProperties);



}
