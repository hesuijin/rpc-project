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


    private final Map<String, Object> serviceMap;
    private final Set<String> registeredService;
    private final ServiceRegistry serviceRegistry;


    public ServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");
    }

    @Override
    public void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties) {

    }

    @Override
    public Object getService(RpcServiceProperties rpcServiceProperties) {
        return null;
    }

    @Override
    public void publishService(Object service, RpcServiceProperties rpcServiceProperties) {
        try {
            //获取 本服务的 IP地址
            String host = InetAddress.getLocalHost().getHostAddress();
            //获取该接口的 接口名称
            Class<?> serviceRelatedInterface = service.getClass().getInterfaces()[0];
            String serviceName = serviceRelatedInterface.getCanonicalName();
            rpcServiceProperties.setServiceName(serviceName);
            this.addService(service, serviceRelatedInterface, rpcServiceProperties);
            //对该接口进行注册  注册在zk中
            //
            serviceRegistry.registerService(rpcServiceProperties.toRpcServiceName(), new InetSocketAddress(host, SocketRpcServer.PORT));
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        String host = InetAddress.getLocalHost().getHostAddress();
        System.out.println(host);
        myTest myTest1 =new myTestImp();
        Class<?> serviceRelatedInterface = myTest1.getClass().getInterfaces()[0];
        System.out.println(serviceRelatedInterface);
    }
}
