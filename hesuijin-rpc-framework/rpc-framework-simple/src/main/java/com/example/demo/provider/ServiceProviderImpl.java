package com.example.demo.provider;

import com.example.common.entity.RpcServiceProperties;
import com.example.common.extension.ExtensionLoader;
import com.example.demo.registryCenter.zookeeper.ServiceRegistry.ServiceRegistry;
import com.example.demo.remotingCenter.transport.socket.SocketRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/4
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider{

    // com.example.api.HelloService  serviceImplObject   存放在 hash集合 serviceMap 中
    private final Map<String, Object> serviceMap;
    // com.example.api.HelloService     存放在 set集合 registeredService 中
    private final Set<String> registeredService;
    private final ServiceRegistry serviceRegistry;


    public ServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();

        serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");
    }



    @Override
    public Object getService(RpcServiceProperties rpcServiceProperties) {
        return null;
    }

    /**
     * 1：构造rpcServiceName   接口所在包 加 名称  com.example.api.HelloService (可能需要加上分组或者版本号)
     * 2：构造inetSocketAddress  斜杆+本机公网地址+端口号  /192.168.137.1:9998
     * 3：使用以上参数 在 zk中注册节点
     * 4：在本类中使用set集合 与hash集合 记录本次新增的   接口实现类的对象  与  接口所在包 + 名称
     *
     * @param serviceImplObject              接口实现类的对象
     * @param rpcServiceProperties service related attributes
     */
    @Override
    public void publishService(Object serviceImplObject, RpcServiceProperties rpcServiceProperties) {
        try {

            //获取该接口的 interface + 接口所在包 + 名称 ：interface com.example.api.HelloService
            Class<?> serviceRelatedInterface = serviceImplObject.getClass().getInterfaces()[0];
            //获取该接口的   接口所在包 + 名称 ：com.example.api.HelloService
            String serviceName = serviceRelatedInterface.getCanonicalName();
            rpcServiceProperties.setServiceName(serviceName);
            String  rpcServiceName= rpcServiceProperties.toRpcServiceName();

            //获取 本服务的 IP地址
            String host = InetAddress.getLocalHost().getHostAddress();
            InetSocketAddress inetSocketAddress =  new InetSocketAddress(host, SocketRpcServer.PORT);

            //对该接口进行注册  注册在zk中
            //入参1：接口所在包 加 名称           com.example.api.HelloService   (可能需要加上分组或者版本号)
            //入参2：斜杆+本机公网地址+端口号  /192.168.137.1:9998
            serviceRegistry.registerService(rpcServiceName,inetSocketAddress);

            //入参1：接口实现类的对象
            //入参2：接口所在包 + 名称 ：com.example.api.HelloService
            this.addService(serviceImplObject, rpcServiceName);

        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }

    @Override
    public void addService(Object serviceImplObject,  String rpcServiceName) {
        //该 com.example.api.HelloService  是否存在
        //已存在 则返回
        //未存在     com.example.api.HelloService                      存放在 set集合 registeredService 中
        //           com.example.api.HelloService  serviceImplObject   存放在 hash集合 serviceMap 中
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, serviceImplObject);
        log.info("Add service: {} and interfaces:{}", rpcServiceName, serviceImplObject.getClass().getInterfaces());
    }


}
