package com.example.demo.registryCenter.zookeeper.ServiceRegistry;

import com.example.demo.registryCenter.zookeeper.CuratorUtils;
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

        //构造将要新增的节点
        //入参1：接口所在包 加 名称           com.example.api.HelloService-group-version(加上群组以及版本号)
        //入参2：斜杆+本机公网地址+端口号  /192.168.137.1:9998
        //最终结构：com.example.api.HelloService-socketSeverNameGroup-socketSeverNameVersion/192.168.137.1:9998
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
