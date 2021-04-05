package com.example.demo.proxy;

import com.example.common.entity.RpcServiceProperties;
import com.example.demo.remotingCenter.transport.RpcRequestTransport;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
@Slf4j
public class RpcClientProxy  implements InvocationHandler {


    private final RpcRequestTransport rpcRequestTransport;
    private final RpcServiceProperties rpcServiceProperties;

    //
    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, RpcServiceProperties rpcServiceProperties) {
        this.rpcRequestTransport = rpcRequestTransport;
        if (rpcServiceProperties.getGroup() == null) {
            rpcServiceProperties.setGroup("");
        }
        if (rpcServiceProperties.getVersion() == null) {
            rpcServiceProperties.setVersion("");
        }
        this.rpcServiceProperties = rpcServiceProperties;
    }

    /**
     * 获取代理类
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getProxy(Class<T> clazz) {
        //三个产生分别是
        //1： 目标类的类加载
        //2： 代理需要实现的接口，可指定多个
        //3： 代理对象对应的自定义 InvocationHandler （重写 invoke）
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * 重新反射方法
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
