package com.example.demo.remotingCenter.transport.socket;

import com.example.common.entity.RpcServiceProperties;
import com.example.common.enums.RpcConstant;
import com.example.common.factory.SingletonFactory;
import com.example.common.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import com.example.demo.config.CustomShutdownHook;
import com.example.demo.provider.ServiceProvider;
import com.example.demo.provider.ServiceProviderImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;

/**
 * @Description: socketRpcServer 不再是单纯的socket服务端 而是 Rpc框架 socket的服务端
 * @Author HeSuiJin
 * @Date 2021/4/3
 */
@Slf4j
public class SocketRpcServer {

    //线程池
    private final ExecutorService threadPool;
    //服务提供类  提供注册与发现功能
    private final ServiceProvider serviceProvider;

    /**
     * 构造方法创建线程池
     */
    public SocketRpcServer() {
        //socket-server-rpc-pool 为线程池名称
        threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    /**
     * 注册相关信息到zk中
     *      1：该接口信息三要素 具有 该服务IP端口这一节点
     *          com.example.api.HelloService-socketSeverNameGroup-socketSeverNameVersion/192.168.137.1:9998
     *      2：实例对象存放到内存中
     *          key：接口信息三要素  com.example.api.HelloService-socketSeverNameGroup-socketSeverNameVersion
     *          value：实例对象  HelloServiceImpl@XXXXXX
     * @param serviceImplObject  实例对象
     * @param rpcServiceProperties  接口信息三要素（接口信息(接口位置+接口类名) - 接口所在组 -接口版本号）
     */
    public void registerService(Object serviceImplObject, RpcServiceProperties rpcServiceProperties) {
        serviceProvider.publishService(serviceImplObject, rpcServiceProperties);
    }

    /**
     * SocketServer 服务端启动
     */
    public void start() {
        try (ServerSocket server = new ServerSocket()) {
            String host = InetAddress.getLocalHost().getHostAddress();
            //服务端启动时绑定端口
            server.bind(new InetSocketAddress(host, RpcConstant.SocketRpcServer.PORT));
            //当服务进行关闭的时候  注册的节点需要删除 并停用该服务线程池
            CustomShutdownHook.getCustomShutdownHook().clearAll();
            Socket socket;
            //从连接请求队列中取出一个客户的连接请求，然后创建与客户连接的Socket对象，
            //接下来，服务器从Socket对象中获得输入流和输出流，就能与客户交换数据。

            //我们可以认为server.accept()获取的Socket客户端对象是新的Socket对象，
            //每一次获取到新的Socket对象，实际上不会影响到前面已经获取的Socket客户端对象。

            //同时当开始接收请求时  则线程池使用某线程执行相关逻辑。
            while ((socket = server.accept()) != null) {
                log.info("client connected [{}]", socket.getInetAddress());
                //线程池 执行SocketRpcRequestHandlerRunnable
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
            //线程池关闭
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("occur IOException:", e);
        }
    }

}
