package com.example.common.utils.concurrent.threadpool;

import lombok.Getter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的7个核心参数   除了生成工厂 和 拒绝策略  其他在这这里设置
 * @Description:线程池常量
 * @Author HeSuiJin
 * @Date 2021/4/3
 */
@Getter
public class CustomThreadPoolConfig {

    /**
     * 线程池默认参数
     */
    //核心线程数
    private static final int DEFAULT_CORE_POOL_SIZE = 10;
    //最大线程池数
    private static final int DEFAULT_MAXIMUM_POOL_SIZE_SIZE = 100;
    //非核心线程保活时间
    private static final int DEFAULT_KEEP_ALIVE_TIME = 1;
    //非核心线程保活时间  单位
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;


    /**
     * 可配置参数
     */
    private int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    private int maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE_SIZE;
    private long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
    private TimeUnit unit = DEFAULT_TIME_UNIT;

    // 使用有界队列
    private static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 100;
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(DEFAULT_BLOCKING_QUEUE_CAPACITY);
}
