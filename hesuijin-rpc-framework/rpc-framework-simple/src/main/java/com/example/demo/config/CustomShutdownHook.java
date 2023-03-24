package com.example.demo.config;

import com.example.common.enums.RpcConstant;
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

    /**
     * 停止线程池
     */
    public void clearAll() {
        //为JDK方法 RunTime.getRunTime().addShutdownHook的作用就是在JVM销毁前执行的一个线程
        //如果我们之前定义了一系列的线程池供程序本身使用,我们可以利用这个性质,就可以在这个最后执行的线程中把这些线程池优雅的关闭掉
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("addShutdownHook for clearAll");
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), RpcConstant.SocketRpcServer.PORT);
                //清除 zk 中该 服务存在的节点
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException ignored) {
            }
            //关闭该服务的所有线程池
            ThreadPoolFactoryUtils.shutDownAllThreadPool();
        }));
    }
}
