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
     * 注册功能
     * @param service              需要放到内存缓存的实例对象
     * @param rpcServiceProperties key；接口三要素：接口信息(接口位置+接口类名) - 接口所在组 -接口版本号
     * @param port  socket(netty) 端口号
     */
    void publishService(Object service, RpcServiceProperties rpcServiceProperties,int port);

    /**
     * 发现功能
     * @param rpcServiceProperties  key：接口三要素：接口信息(接口位置+接口类名) - 接口所在组 -接口版本号
     * @return 获取内存缓存的实例对象
     */
    Object getService(RpcServiceProperties rpcServiceProperties);
}


//    /**
//     * @param service              serviceImplObject
//     * @param rpcServiceName service related attributes
//     */
//    void addService(Object service,  String rpcServiceName);