package com.example.demo.proxyDemo.jdkDynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @Description: 自定义MyInvocationHandler类
 * @Author HeSuiJin
 * @Date 2021/3/29
 */
public class MyInvocationHandler implements InvocationHandler {

    /**
     * 代理类中的真实对象
     */
    private final Object target;

    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    /**
     * @param proxy   代理对象
     * @param method  方法
     * @param args    参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //调用方法之前，我们可以添加自己的操作
        System.out.println("我是自定义逻辑啊 before method " + method.getName());
        Object result = method.invoke(target, args);
        //调用方法之后，我们同样可以添加自己的操作
        System.out.println("我是自定义逻辑啊 after method " + method.getName());
        return result;
    }
}
