package com.example.rpc.remoting.transport.proxy.jdkDynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 自定义MyInvocationHandler类
 * @Description:
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
