package com.example.demo.registry.zookeeper.ServiceRegistry;

import com.example.demo.registry.zookeeper.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * @Description:
 * 创建持久化节点
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
public class ServiceRegistryImpl implements ServiceRegistry {

    /**用于创建持久化节点
     * @param rpcServiceName
     * @param inetSocketAddress
     */
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
//        CuratorFramework zkClient = CuratorUtils.getZkClient();
//        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
