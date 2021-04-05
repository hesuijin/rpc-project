package com.example.common.utils.concurrent.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @Description:
 * 创建线程池（ThreadPool）的工具类
 * @Author HeSuiJin
 * @Date 2021/4/3
 */
@Slf4j
public class ThreadPoolFactoryUtils {

    /**
     * 通过 threadNamePrefix 来区分不同线程池（我们可以把相同 threadNamePrefix 的线程池看作是为同一业务场景服务）。
     * key: threadNamePrefix
     * value: threadPool
     */
    private static final Map<String, ThreadPoolExecutor> THREAD_POOLS = new ConcurrentHashMap<>();

    private ThreadPoolFactoryUtils() {

    }

    /**
     * 获取默认线程池  使用默认配置
     * @param threadNamePrefix
     * @return
     */
    public static ThreadPoolExecutor createCustomThreadPoolIfAbsent(String threadNamePrefix) {
        CustomThreadPoolConfig customThreadPoolConfig = new CustomThreadPoolConfig();
        //设置 为非守护线程池
        //TODO 设置为非守护线程理解
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    /**
     * 如果线程池不存在则自建一个
     * @param customThreadPoolConfig
     * @param threadNamePrefix
     * @param daemon
     * @return
     */
    public static ThreadPoolExecutor createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon) {
        //判断该线程池是否在 Map集合里面 如果不存在则创建
        ThreadPoolExecutor threadPool = THREAD_POOLS.computeIfAbsent(threadNamePrefix, k -> createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon));
        // 如果 threadPool 被 shutdown 或者 处于terminate状态  的话就重新创建一个
        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            THREAD_POOLS.remove(threadNamePrefix);
            threadPool = createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon);
            THREAD_POOLS.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }

    private static ThreadPoolExecutor createThreadPool(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon) {
        //设置生成工厂
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);

        //输入6个核心参数 第7个核心参数 拒绝参数使用默认策略  线程池中 线程最大线程数 + 队列容量 < 任务量 则抛出异常
        return new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(), customThreadPoolConfig.getMaximumPoolSize(),
                customThreadPoolConfig.getKeepAliveTime(), customThreadPoolConfig.getUnit(), customThreadPoolConfig.getWorkQueue(),
                threadFactory);
    }




    /**
     * 创建 ThreadFactory 。如果threadNamePrefix不为空 则使用自建ThreadFactory 自定义名称
     * 否则使用defaultThreadFactory
     * @param threadNamePrefix 作为创建的线程名字的前缀
     * @param daemon           指定是否为 Daemon Thread(守护线程)
     * @return ThreadFactory
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }


    /**
     * shutdown方法：平滑的关闭ExecutorService，当此方法被调用时，
     * ExecutorService停止接收新的任务并且等待已经提交的任务（包含提交正在执行和提交未执行）执行完成。
     * 当所有提交任务执行完毕，线程池即被关闭。
     *
     * awaitTermination方法：接收人timeout和TimeUnit两个参数，用于设定超时时间及单位。
     * 当等待超过设定时间时，会监测ExecutorService是否已经关闭，若关闭则返回true，否则返回false。
     * 一般情况下会和shutdown方法组合使用。
     *
     * shutdownNow()：直接关闭 停止当前的任务
     */
    public static void shutDownAllThreadPool() {
        log.info("进行关闭所有线程池操作 ");
        THREAD_POOLS.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("平滑 关闭该线程池 [{}] [{}]", entry.getKey(), executorService.isTerminated());
            boolean isFinishShutdown = false;
            try {
                isFinishShutdown= executorService.awaitTermination(20, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("线程池没有被关闭，需要立即执行关闭线程池 ");
            }

            //shutdown关闭失败  或者  awaitTermination 异常都执行立即关闭命令
            if (!isFinishShutdown ){
                executorService.shutdownNow();
                log.info("立即  关闭该线程池 [{}] [{}]", entry.getKey(), executorService.isTerminated());
            }
        });
    }
//
//
//    /**
//     * 打印线程池的状态
//     *
//     * @param threadPool 线程池对象
//     */
//    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool) {
//        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status", false));
//        scheduledExecutorService.scheduleAtFixedRate(() -> {
//            log.info("============ThreadPool Status=============");
//            log.info("ThreadPool Size: [{}]", threadPool.getPoolSize());
//            log.info("Active Threads: [{}]", threadPool.getActiveCount());
//            log.info("Number of Tasks : [{}]", threadPool.getCompletedTaskCount());
//            log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());
//            log.info("===========================================");
//        }, 0, 1, TimeUnit.SECONDS);
//    }

}
