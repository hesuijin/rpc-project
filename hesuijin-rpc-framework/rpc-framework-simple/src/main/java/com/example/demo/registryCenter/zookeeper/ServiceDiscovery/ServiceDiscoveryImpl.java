package com.example.demo.registryCenter.zookeeper.ServiceDiscovery;

import com.example.common.enums.RpcErrorMessageEnum;
import com.example.common.exception.RpcException;
import com.example.common.extension.ExtensionLoader;
import com.example.demo.loadbalance.LoadBalance;
import com.example.demo.registryCenter.zookeeper.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@Slf4j
public class ServiceDiscoveryImpl implements ServiceDiscovery{

    private final LoadBalance loadBalance;

    public ServiceDiscoveryImpl() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {

        //获取Zookeeper客户端
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        //获取rpcServiceName 下所有子节点的路径
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (serviceUrlList == null || serviceUrlList.size() == 0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        //负载均衡策略  所有字节点路径
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcServiceName);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        //进行截取
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);


    }
}
