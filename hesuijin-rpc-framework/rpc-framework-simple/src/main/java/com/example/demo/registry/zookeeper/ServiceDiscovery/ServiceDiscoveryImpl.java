package com.example.demo.registry.zookeeper.ServiceDiscovery;

import com.example.common.enums.RpcErrorMessageEnum;
import com.example.common.exception.RpcException;
import com.example.demo.registry.zookeeper.CuratorUtils;
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

    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        //获取Zookeeper客户端
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        //获取所有字节点
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (serviceUrlList == null || serviceUrlList.size() == 0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        //负载均衡策略
//        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcServiceName);
//        log.info("Successfully found the service address:[{}]", targetServiceUrl);
//        String[] socketAddressArray = targetServiceUrl.split(":");
//        String host = socketAddressArray[0];
//        int port = Integer.parseInt(socketAddressArray[1]);
//        return new InetSocketAddress(host, port);

        return null;
    }
}
