package com.example.demo.registry.zookeeper;

import org.apache.curator.framework.CuratorFramework;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * CuratorUtils 工具类 可以查看zookeeperDemo里面的相关方法
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
public class CuratorUtils {

    //睡眠等待时间
    private static final int BASE_SLEEP_TIME = 1000;
    //重试次数
    private static final int MAX_RETRIES = 3;
    //注册根路径地址
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";

    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();

    // Zookeeper 服务端
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "47.113.101.241:2181";
    // Zookeeper 客户端 引入CuratorFramework对象  Zookeeper 客户端
    private static CuratorFramework zkClient;

    private CuratorUtils() {
    }



}
