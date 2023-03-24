package com.example.demo.registryCenter.zookeeper;

import com.alibaba.fastjson.JSONObject;
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
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

// *  服务端（注册者）：使用  接口信息三要素 + 服务端IP端口形成  接口信息三要素/服务端IP端口  形成持久化节点到Zk中
//  *  客户端（发现者）：使用  接口信息三要素 获取全部的节点  然后使用负载均衡的方式获取其中一个节点的服务端IP端口  进行调用

/**
 * @Description: 注意 请先查看 Zookeeper的相关知识点  结构为树  以node节点组成
 * CuratorUtils 工具类 可以查看zookeeperDemo里面的相关方法
 *
 *  1：生成zkClient
*   2：获取rpcServiceClassName 下的所有子节点，如果是rpcServiceClassName是首次获取，则需要注册到Zk中，从而被动态监听。
*      rpcServiceClassName ：包含className（interfaceName）服务接口类名称如 com.example.demo.HelloService（group + version）
*      子节点 ：  服务端注册到Zookeeper的IP+端口号  127.0.0.1:8080   127.0.0.2:8080  127.0.0.3:8080
 * 最终数据结构（rpcServiceClassName）:
 *      1:com.example.demo.HelloService-group-version/127.0.0.1:8080
 *      2:com.example.demo.HelloService-group-version/127.0.0.2:8080
 *      3:com.example.demo.HelloService-group-version/127.0.0.3:8080
 *
 *
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

    //已经注册的节点集合  用于后面具体使用 rpcServiceClassName为key  rpcServiceClassName下的子节点为value
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    //已经注册的节点集合  用于后面剔除
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();

    // Zookeeper 服务端
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "47.113.101.241:2181";
    // Zookeeper 客户端 引入CuratorFramework对象  Zookeeper 客户端
    private static CuratorFramework zkClient;

    private CuratorUtils() {
    }

    /**
     * //获取ZkClient客户端(实际上该客户端直接连接Zk的服务端)
     * @return
     */
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
                throw new RuntimeException("CuratorUtils工具类 创建ZkClient 等待连接Zookeeper超时");
            }
        } catch (InterruptedException e) {
            log.error("CuratorUtils工具类 创建ZkClient 获取ZkClient异常" + e.getMessage(), e);
        }
        return zkClient;
    }

    /**
     * 注册一个持久化节点   该节点路径 ： /my-rpc/com.example.api.HelloService-group-version/192.168.137.1:9998
     * @param zkClient  zk客户端 （实际直接连接其服务端）
     * @param path     节点路径
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("CuratorUtils工具类 创建节点 该节点已经存在 该节点是 :[{}]", path);
            } else {
                // /my-rpc/com.example.api.HelloService/192.168.137.1:9998
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("CuratorUtils工具类 创建节点 该节点已经完成创建 该节点是:[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            log.error("CuratorUtils工具类 创建节点 create persistent node for path [{}] fail", path);
        }

    }

    /**
     * 目标：获取rpcServiceClassName节点下的子节点
     *
     * 1:先判断rpcServiceClassName节点是否存在内存缓存SERVICE_ADDRESS_MAP中，如果存在则获取其子节点直接返回。
     * 2：如果内存缓存中不存在 则
     *      1：使用getChildrenNodes去Zk中获取 rpcServiceClassName的子节点
     *      2：同时由于在SERVICE_ADDRESS_MAP中不存在，
     *         那意味着该rpcServiceClassName大概率是没有被注册过的rpcServiceClassName（也有可能其子节点都被清除了）
     *         因为新的rpcServiceClassName 需要注册到Zk中，后续Zk监听器可以监听该rpcServiceClassName下的节点变化
     *         如果其子节点发生变化，则同步到SERVICE_ADDRESS_MAP中。
     * @param zkClient
     * @param rpcServiceClassName
     * @return
     */
    public static List<String> getChildrenNodesAndFirstRegisterWatcher(CuratorFramework zkClient, String rpcServiceClassName){
        //判断rpcServiceClassName节点是否存在内存缓存SERVICE_ADDRESS_MAP中
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceClassName)) {
            log.info("CuratorUtils工具类 rpcServiceClassName[{}]的子节点，已经存在[{}]", rpcServiceClassName,JSONObject.toJSONString(SERVICE_ADDRESS_MAP.get(rpcServiceClassName)));
            return SERVICE_ADDRESS_MAP.get(rpcServiceClassName);
        }

        List<String> result = null;

        //使用getChildrenNodes去Zk中获取 rpcServiceClassName的子节点
        log.info("CuratorUtils工具类 getChildrenNodes 获取:[{}]的子节点 ", JSONObject.toJSONString(rpcServiceClassName));
        result = getChildrenNodes(zkClient,rpcServiceClassName);
        log.info("CuratorUtils工具类 getChildrenNodes 获取子节点结果:[{}]", JSONObject.toJSONString(result));

        //把rpcServiceClassName注册到zkClient中
        //从而让rpcServiceClassName被zkClient监听
        try {
            registerWatcher(rpcServiceClassName, zkClient);
        } catch (Exception e) {
            log.error("CuratorUtils工具类 set registerWatcher nodes for path [{}] fail", rpcServiceClassName);
        }
        return result;
    }

    /**
     *  获取rpcServiceClassName 下所有子节点
     *
     * @param zkClient
     * @param rpcServiceClassName
     * @return
     */
    private static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceClassName) {

        List<String> result = null;
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceClassName;
        try {
            //获取该节点的所有子节点路径 然后设置到内存的SERVICE_ADDRESS_MAP中
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceClassName, result);
        } catch (Exception e) {
            log.error("get children nodes for path [{}] fail", servicePath);
        }
        return result;
    }

    /**
     * 注册监听器
     * 如果发生节点的变化，可以自定义回调动作,如：获取该节点的所有子节点路径  并添加到  这个节点处为key值的 map集合中
     *  如：servicePath为rpcServiceClassName  则意味着开通rpcServiceClassName的监听，
     *  后续如果rpcServiceClassName下有子节点发生变化都会触发该监听器。
     *
     *  从监听器获取到数据后 存放到SERVICE_ADDRESS_MAP中，作为内存缓存使用。
     *
     * @param rpcServiceClassName
     * @param zkClient
     * @throws Exception
     */
    private static void registerWatcher(String rpcServiceClassName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceClassName;

        //生成监听器 当servicePath的子节点发生改变的时候 执行逻辑
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);

        //回调动作
        //registerWatcher监听指定的节点  如果该节点的子节点发生变化则触发
        //具体变化类型 可查看该枚举PathChildrenCacheEvent.Type
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            log.info("CuratorUtils工具类  registerWatcher  监听:{}的子节点节点发生变化了，进行了{}操作",servicePath,pathChildrenCacheEvent.getType());
            //当节点发生改变时
            //获取该节点的所有子节点路径  并添加到  这个节点处为key值的 map集合中
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceClassName, serviceAddresses);
        };

        //pathChildrenCacheListener 加入到pathChildrenCache中
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);

        //监听器开始运行
        pathChildrenCache.start();
        log.info("CuratorUtils工具类  registerWatcher 开始设置监听节点:[{}]", JSONObject.toJSONString(servicePath));
    }


    /**
     * 删除zkClient 下面该服务 的所有节点数据
     * 即尾部等于  已经停止服务端的 IP:端口号 时，剔除掉该服务端的数据。
     */
    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress) {
        String inetSocketAddressString = inetSocketAddress.toString();
        log.info("清除服务器：[{}]在zk的所有节点！",inetSocketAddressString);

        //REGISTERED_PATH_SET集合  对 inetSocketAddress /192.168.137.1:9998  进行遍历
        REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
            try {
                if (p.endsWith(inetSocketAddressString)) {
                    //如果存在 p的部分等于  inetSocketAddress  那么删除这个单个节点
                    zkClient.delete().forPath(p);

                    REGISTERED_PATH_SET.remove(p);
                    log.info("CuratorUtils工具类 clearRegistry 清除服务器：[{}]在zk的所有节点,清除成功！",JSONObject.toJSONString(p));
                }
            } catch (Exception e) {
                log.error("CuratorUtils工具类 clearRegistry 清除服务器：[{}]在zk的所有节点, 失败！", p);
            }
        });
    }

}
