package com.example.demo.provider;

import com.example.common.entity.RpcServiceProperties;

/**
 * @Description:
 * 服务提供类 提供注册与发现功能
 * @Author HeSuiJin
 * @Date 2021/4/3
 */
public interface ServiceProvider {

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


//    /**
//     * @param service              serviceImplObject
//     * @param rpcServiceName service related attributes
//     */
//    void addService(Object service,  String rpcServiceName);