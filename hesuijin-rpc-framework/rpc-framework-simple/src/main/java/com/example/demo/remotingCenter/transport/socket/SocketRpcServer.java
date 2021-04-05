package com.example.demo.remotingCenter.transport.socket;

import com.example.common.entity.RpcServiceProperties;
import com.example.common.factory.SingletonFactory;
import com.example.common.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import com.example.demo.config.CustomShutdownHook;
import com.example.demo.provider.ServiceProvider;
import com.example.demo.provider.ServiceProviderImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/3
 */
@Slf4j
public class SocketRpcServer {

    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;
    public static final int PORT = 9998;

    public SocketRpcServer() {
        threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    public void registerService(Object service, RpcServiceProperties rpcServiceProperties) {
        serviceProvider.publishService(service, rpcServiceProperties);
    }

    public void start() {
        try (ServerSocket server = new ServerSocket()) {
            String host = InetAddress.getLocalHost().getHostAddress();
            server.bind(new InetSocketAddress(host, PORT));

            //当服务进行关闭的时候  注册的节点需要删除 并停用该服务线程池
            CustomShutdownHook.getCustomShutdownHook().clearAll();
            Socket socket;
            while ((socket = server.accept()) != null) {
                log.info("client connected [{}]", socket.getInetAddress());
                //TODO 待补充
//                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            log.error("occur IOException:", e);
        }
    }
}
