package com.example.demo.remotingCenter.transport.socket;

import com.example.common.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import com.example.demo.provider.ServiceProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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


    public SocketRpcServer() {
        threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("socket-server-rpc-pool");
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

}
