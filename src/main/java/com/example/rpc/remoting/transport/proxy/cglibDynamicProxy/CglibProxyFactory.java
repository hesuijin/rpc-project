package com.example.rpc.remoting.transport.proxy.cglibDynamicProxy;

import net.sf.cglib.proxy.Enhancer;

/**
 * 通过工厂的类获取代理对象
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/3/29
 */
public class CglibProxyFactory {

    public static Object getProxy(Class<?> clazz) {
        // 创建动态代理增强类
        Enhancer enhancer = new Enhancer();
        // 设置类加载器
        enhancer.setClassLoader(clazz.getClassLoader());
        // 设置被代理类
        enhancer.setSuperclass(clazz);
        // 设置方法拦截器
        enhancer.setCallback(new MyMethodInterceptor());
        // 创建代理类
        return enhancer.create();
    }
}
