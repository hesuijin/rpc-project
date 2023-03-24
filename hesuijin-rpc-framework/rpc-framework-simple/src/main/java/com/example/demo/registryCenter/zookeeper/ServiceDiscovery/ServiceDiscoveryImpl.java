package com.example.demo.registryCenter.zookeeper.ServiceDiscovery;

import com.example.common.enums.RpcErrorMessageEnum;
import com.example.common.exception.RpcException;
import com.example.common.extension.ExtensionLoader;
import com.example.demo.loadbalance.LoadBalance;
import com.example.demo.loadbalance.abstractLoadBalance.RandomLoadBalance;
import com.example.demo.registryCenter.zookeeper.CuratorUtils;
import com.example.demo.registryCenter.zookeeper.ServiceRegistry.ServiceRegistry;
import com.example.demo.registryCenter.zookeeper.ServiceRegistry.ServiceRegistryImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

//TODO  后面再看 通过另一种方式加载loadBalance


/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@Slf4j
public class ServiceDiscoveryImpl implements ServiceDiscovery{

    private final LoadBalance loadBalance;

    //读取配置 resources/META-INF/extensions  获取loadBalance 指定的对象
    //如key为loadBalance 指向com.example.demo.loadbalance.abstractLoadBalance.RandomLoadBalance
//    this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");

    public ServiceDiscoveryImpl() {
        //负载均衡策略 使用随机策略
        this.loadBalance = new RandomLoadBalance();
    }

    /**
     * 通过负载均衡获取  服务端的IP端口
     * 通过入参rpcServiceClassName 指定获取服务端对应的接口类名称
     *
     * @param rpcServiceClassName  服务端的接口类名称如 com.example.demo.HelloService
     * @return
     */
    @Override
    public InetSocketAddress lookupService(String rpcServiceClassName) {

        //1：获取Zookeeper客户端
        CuratorFramework zkClient = CuratorUtils.getZkClient();

        // 2：获取rpcServiceClassName 下的所有子节点
        //      rpcServiceClassName ：包含className（interfaceName）服务接口类名称 com.example.demo.HelloService
        //      子节点 ：  服务端注册到Zookeeper的IP+端口号  127.0.0.1:8080   127.0.0.2:8080  127.0.0.3:8080
        List<String> serviceUrlList = CuratorUtils.getChildrenNodesAndFirstRegisterWatcher(zkClient, rpcServiceClassName);
        if (serviceUrlList == null || serviceUrlList.size() == 0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceClassName);
        }

        //3：负载均衡策略  选取其中一个子节点  （即拿到对应服务端的 IP：端口号）

        //在接口类中loadBalance应该需要考虑多个关键参数作为入参的情况
        //rpcServiceClassName 在负载均衡策略中在Hash一致性算法策略中会作为参数使用，但在随机策略就不需要了。
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcServiceClassName);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        //进行截取
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);

    }
}
