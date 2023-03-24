package com.example.client.clientStart;

import com.example.api.Hello;
import com.example.api.HelloService;
import com.example.common.entity.RpcServiceProperties;
import com.example.demo.proxy.RpcClientProxy;
import com.example.demo.remotingCenter.transport.RpcRequestTransport;
import com.example.demo.remotingCenter.transport.socket.SocketRpcClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * 1：客户端通过接口三要素  在zk中找到对应的Socket服务端  并进行调用
 * 2：服务端通过接口三要素  找到缓存到内存里面的对应实例对象信息  根据客户端传递的入参执行相应的逻辑
 *         RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
 *                 .paramTypes(method.getParameterTypes())
 *                 .parameters(args)
 *                 .requestId(UUID.randomUUID().toString())
 *                 .interfaceName(method.getDeclaringClass().getName())
 *                 .group(rpcServiceProperties.getGroup())
 *                 .version(rpcServiceProperties.getVersion())
 *                 .build();
 *
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
@Slf4j
public class SocketClientMain {

    public static void main(String[] args) {

        //获取ServiceDiscoveryImpl实现类  单例的调用方
        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();

        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("socketSeverNameGroup").version("socketSeverNameVersion").build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceProperties);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        helloService.getClass().getName();

        //发送信息给HelloService接口类 通过指定对应的组以及版本号  socketSeverNameGroup  socketSeverNameVersion
        String helloResult = helloService.helloServiceApi(new Hello("测试socket发送信息", "测试socket"));
        log.info("发送信息给HelloService接口类 分组socketSeverNameGroup 版本号socketSeverNameVersion 收到回复："+helloResult);

        RpcServiceProperties rpcServicePropertiesOther = RpcServiceProperties.builder()
                .group("socketSeverNameGroupOther").version("socketSeverNameVersionOther").build();
        RpcClientProxy rpcClientProxyOhther = new RpcClientProxy(rpcRequestTransport, rpcServicePropertiesOther);
        //发送信息给HelloService接口类 通过指定对应的组以及版本号  socketSeverNameGroupOther  socketSeverNameVersionOther
        HelloService helloServiceOther = rpcClientProxyOhther.getProxy(HelloService.class);
        String helloResultOther = helloServiceOther.helloServiceApi(new Hello("测试socket发送信息 Other", "测试socket Other"));
        log.info("发送信息给HelloService接口类 分组socketSeverNameGroupOther 版本号socketSeverNameVersionOther 收到回复："+helloResultOther);
    }

}
