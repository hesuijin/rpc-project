package com.example.demo.registryCenter.zookeeper.ServiceDiscovery;

import com.example.common.enums.RpcErrorMessageEnum;
import com.example.common.exception.RpcException;
import com.example.common.extension.ExtensionLoader;
import com.example.demo.loadbalance.LoadBalance;
import com.example.demo.registryCenter.zookeeper.CuratorUtils;
import com.example.demo.registryCenter.zookeeper.ServiceRegistry.ServiceRegistry;
import com.example.demo.registryCenter.zookeeper.ServiceRegistry.ServiceRegistryImpl;
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
//         ExtensionLoader<LoadBalance> extensionLoader = ExtensionLoader.getExtensionLoader(LoadBalance.class);
//        this.loadBalance = extensionLoader.getExtension("loadBalance");

        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    @Override
    public InetSocketAddress lookupService(String rpcServiceClassName) {

        //1：获取Zookeeper客户端
        CuratorFramework zkClient = CuratorUtils.getZkClient();

        // 2：获取rpcServiceClassName 下的所有子节点
        //      rpcServiceClassName ：包含className（interfaceName）服务接口名称 com.example.demo.HelloService
        //      子节点 ：  服务端注册到Zookeeper的IP+端口号  127.0.0.1:1   127.0.0.1:2  127.0.0.1:3
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceClassName);
        if (serviceUrlList == null || serviceUrlList.size() == 0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceClassName);
        }

        //3：负载均衡策略  选取其中一个子节点  （即拿到对应的 IP：端口号）
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcServiceClassName);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        //进行截取
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);

    }
}
