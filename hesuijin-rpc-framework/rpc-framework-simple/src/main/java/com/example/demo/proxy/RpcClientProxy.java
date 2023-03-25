package com.example.demo.proxy;

import com.example.common.entity.RpcServiceProperties;
import com.example.common.enums.RpcErrorMessageEnum;
import com.example.common.enums.RpcResponseCodeEnum;
import com.example.common.exception.RpcException;
import com.example.demo.remotingCenter.dto.RpcRequest;
import com.example.demo.remotingCenter.dto.RpcResponse;
import com.example.demo.remotingCenter.transport.RpcRequestTransport;
import com.example.demo.remotingCenter.transport.socket.SocketRpcClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @Description: RpcClientProxy代理类
 * 使用泛型获取原类的对应的代理类 ： (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this)
 * 后续执行代理类对应实例对象的任何方法都改为使用重写的invoke方法。
 * 重写的invoke的逻辑中执行特殊的逻辑。
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
@Slf4j
public class RpcClientProxy  implements InvocationHandler {


    private static final String INTERFACE_NAME = "interfaceName";

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

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceProperties = RpcServiceProperties.builder().group("").version("").build();
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
     * 通过代理的方式获取对象  在执行任何方法时都会执行  重写的invoke方法
     * invoke方法中可以执行特殊逻辑
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        log.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args)
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .group(rpcServiceProperties.getGroup())
                .version(rpcServiceProperties.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = null;
        //TODO 新增NettyRpcClient后再打开该注释
//        if (rpcRequestTransport instanceof NettyRpcClient) {
//            CompletableFuture<RpcResponse<Object>> completableFuture = (CompletableFuture<RpcResponse<Object>>) rpcRequestTransport.sendRpcRequest(rpcRequest);
//            rpcResponse = completableFuture.get();
//        }
        if (rpcRequestTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<Object>) rpcRequestTransport.sendRpcRequest(rpcRequest);
        }
        this.check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
