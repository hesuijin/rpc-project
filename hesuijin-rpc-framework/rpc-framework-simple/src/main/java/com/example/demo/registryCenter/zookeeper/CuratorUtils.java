package com.example.demo.registryCenter.zookeeper;

import com.example.common.enums.RpcConfigEnum;
import com.example.common.utils.PropertiesFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 注意 请先查看 Zookeeper的相关知识点  结构为树  以node节点组成 （key:value）（路径：值）
 * CuratorUtils 工具类 可以查看zookeeperDemo里面的相关方法
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@Slf4j
public class CuratorUtils {

    //睡眠等待时间
    private static final int BASE_SLEEP_TIME = 1000;
    //重试次数
    private static final int MAX_RETRIES = 3;
    //注册根路径地址
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";

    //服务节点名称为rpcServiceName 下的子节点   /my-rpc/rpcServiceName
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();

    // Zookeeper 服务端
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "47.113.101.241:2181";
    // Zookeeper 客户端 引入CuratorFramework对象  Zookeeper 客户端
    private static CuratorFramework zkClient;

    private CuratorUtils() {
    }

    //获取ZkClient客户端
    public static CuratorFramework getZkClient() {

        // 如果zkClient 已经被创建 直接返回
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }

        // 获取相应的静态资源
        // 如果有相关的Zookeeper静态资源在配置文件 则使用里面的连接地址
        // 否则 使用默认的连接地址
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH.getPropertyValue());
        //获取连接地址
        String zookeeperAddress = properties != null && properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) != null ? properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getPropertyValue()) : DEFAULT_ZOOKEEPER_ADDRESS;

        // 设置重试策略 重试3次 每次间隔1秒
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                // the server to connect to (can be a server list)
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        try {
            // wait 30s until connect to the zookeeper
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("等待连接Zookeeper超时");
            }
        } catch (InterruptedException e) {
            log.info("获取ZkClient异常" + e.getMessage(), e);
        }
        return zkClient;
    }

    /**
     *  获取rpcServiceName 下所有子节点的路径
     *
     * @param zkClient
     * @param rpcServiceName
     * @return
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> result = null;
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        try {
            //获取该节点的所有子节点路径 并 返回
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            //注册监听
            registerWatcher(rpcServiceName, zkClient);
        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail", servicePath);
        }
        return result;
    }

    private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;

        //生成监听器 当servicePath的子节点发生改变的时候 执行逻辑
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            //当字节点发生改变时
            //获取该节点的所有子节点路径  并添加到  这个节点处为key值的 map集合中
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        };

        //pathChildrenCacheListener 加入到pathChildrenCache中
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        //监听器开始运行
        pathChildrenCache.start();
    }
}