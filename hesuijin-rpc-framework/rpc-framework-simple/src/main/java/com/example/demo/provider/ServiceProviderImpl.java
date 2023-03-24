package com.example.demo.provider;

import com.example.common.entity.RpcServiceProperties;
import com.example.common.enums.RpcConstant;
import com.example.common.enums.RpcErrorMessageEnum;
import com.example.common.exception.RpcException;
import com.example.common.extension.ExtensionLoader;
import com.example.demo.registryCenter.zookeeper.ServiceRegistry.ServiceRegistry;
import com.example.demo.registryCenter.zookeeper.ServiceRegistry.ServiceRegistryImpl;
import com.example.demo.remotingCenter.transport.socket.SocketRpcServer;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 服务提供类  提供注册与发现功能
 * publishService方法：
 *       1：把接口三要素（接口信息(接口位置+接口类名) - 接口所在组 -接口版本号） + 服务端对应的信息（IP 端口） 存入zk节点中
 *       2：:把对应的实例对象存放到内存缓存中   key为：接口三要素 value为：实例对象
 * getService方法：根据需要的rpcServiceProperties的接口三要素 获取对应的实例对象
 * @Author HeSuiJin
 * @Date 2021/4/4
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    //线程安全
    // com.example.api.HelloService（group + version）为key   serviceImplObject 为value  存放在 hash集合 serviceMap 中
    private final Map<String, Object> serviceMap;
    //线程安全
    //com.example.api.HelloService（group + version）     存放在 set集合 registeredService 中
    private final Set<String> registeredService;

    private final ServiceRegistry serviceRegistry;

    public ServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        ExtensionLoader<ServiceRegistry> extensionLoader = ExtensionLoader.getExtensionLoader(ServiceRegistry.class);
        this.serviceRegistry = extensionLoader.getExtensionInstance("zk");
    }


    /**
     * 根据需要的rpcServiceProperties的接口三要素 获取对应的实例对象
     * @param rpcServiceProperties service related attributes
     * @return
     */
    @Override
    public Object getService(RpcServiceProperties rpcServiceProperties) {
        Object service = serviceMap.get(rpcServiceProperties.toRpcServiceName());
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    /**
     * @Description:
     * 把接口三要素（接口信息(接口位置+接口类名) - 接口所在组 -接口版本号） + 服务端对应的信息（IP 端口） 存入zk节点中
     * 把对应的实例对象存放到内存缓存中   key为：接口三要素 value为：实例对象
     *
     * 1：构造rpcServiceName   接口所在包 加 名称           com.example.api.HelloService   (需要加上分组以及版本号)
     * 2：构造inetSocketAddress  斜杆+本机公网地址+端口号  /192.168.137.1:9998
     * 3：使用以上参数 在 zk中注册节点
     * 4：在本类中使用set集合 与hash集合 记录本次新增的   接口实现类的对象  与  接口所在包 + 名称
     *
     * @param serviceImplObject    接口实现类的实例对象
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
            String rpcServiceName = rpcServiceProperties.toRpcServiceName();

            //获取 本服务的 IP地址
            String host = InetAddress.getLocalHost().getHostAddress();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(host, RpcConstant.SocketRpcServer.PORT);

            //对该接口信息进行注册  注册到zk中
            //入参1：接口所在包 加 名称           com.example.api.HelloService    (需要加上分组以及版本号)
            //入参2：斜杆+本机公网地址+端口号  /192.168.137.1:9998
            serviceRegistry.registerService(rpcServiceName, inetSocketAddress);

            //入参1：接口实现类的实例对象
            //入参2：接口所在包 + 名称 ：com.example.api.HelloService
            this.addService(serviceImplObject, rpcServiceName);

        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }

    /**
     * 存放到内存中
     * @param serviceImplObject
     * @param rpcServiceName    service related attributes
     */
    private void addService(Object serviceImplObject, String rpcServiceName) {
        //该 com.example.api.HelloService  是否存在
        //已存在 则返回
        //未存在     com.example.api.HelloService                      存放在 set集合 registeredService 中
        //           com.example.api.HelloService  serviceImplObject   存放在 hash集合 serviceMap 中
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        //key  是rpcServiceProperties 接口三要素   value 是对应的实例对象
        serviceMap.put(rpcServiceName, serviceImplObject);
        log.info("Add service: {} and interfaces:{} and serviceImplObject:{}", rpcServiceName, serviceImplObject.getClass().getInterfaces(),serviceImplObject.toString());
    }


}
