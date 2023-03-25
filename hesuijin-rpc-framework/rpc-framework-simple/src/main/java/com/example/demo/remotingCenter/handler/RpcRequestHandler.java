package com.example.demo.remotingCenter.handler;

import com.example.common.exception.RpcException;
import com.example.common.factory.SingletonFactory;
import com.example.demo.provider.ServiceProvider;
import com.example.demo.provider.ServiceProviderImpl;
import com.example.demo.remotingCenter.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2023/3/20
 */
@Slf4j
public class RpcRequestHandler {

    //服务提供类 （提供注册与发现功能）
    private final ServiceProvider serviceProvider;
    public RpcRequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    /**
     * @param rpcRequest  客户端请求对象体
     * @return 最终响应结果
     */
    public Object handle(RpcRequest rpcRequest) {
        //获取实例对象
        Object service = serviceProvider.getService(rpcRequest.toRpcProperties());
        Object result = invokeTargetMethod(rpcRequest, service);
        return result;
    }

    /**
     * @param rpcRequest 客户端请求对象体
     * @param service    实例对象
     * @return 执行相应方法响应结果
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            //Object中的JDK自带getClass方式  获取实现类信息
            //从rpcRequest获取 方法信息  参数类型  具体参数 然后进行调用
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }

}
