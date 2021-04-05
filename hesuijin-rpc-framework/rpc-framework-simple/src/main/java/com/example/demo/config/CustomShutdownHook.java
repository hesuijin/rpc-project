package com.example.demo.config;

import com.example.common.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import com.example.demo.registryCenter.zookeeper.CuratorUtils;
import com.example.demo.remotingCenter.transport.socket.SocketRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @Description:
 * 当服务进行关闭的时候  需要把服务注册的节点给关闭  同时把服务对应的线程池给关闭
 * @Author HeSuiJin
 * @Date 2021/4/4
 */
@Slf4j
public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), SocketRpcServer.PORT);
                //清除 zk 中该 服务存在的节点
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException ignored) {
            }
            //关闭所有线程池
            //TODO 是否需要修改成关闭对应服务的线程池
            ThreadPoolFactoryUtils.shutDownAllThreadPool();
        }));
    }
}
