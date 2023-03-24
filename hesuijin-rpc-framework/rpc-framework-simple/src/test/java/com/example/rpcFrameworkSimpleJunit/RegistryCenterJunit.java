package com.example.rpcFrameworkSimpleJunit;

import com.example.junitDTO.HelloService;
import com.example.junitDTO.HelloServiceImpl;
import com.example.common.entity.RpcServiceProperties;
import com.example.demo.registryCenter.zookeeper.ServiceDiscovery.ServiceDiscoveryImpl;

import com.example.demo.remotingCenter.transport.socket.SocketRpcServer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2023/3/12
 */
@SpringBootTest
public class RegistryCenterJunit {

    /**
     * 服务端 把接口注册到Zk上
     * 最终的目的是ServiceRegistryImpl实现类的注册接口方法：registerService
     * 入参为  String rpcServiceName  +  InetSocketAddress inetSocketAddress
     */
    @Before
    public  void serverRegisterZkTest() {
        //设置需要注册的接口的  group 和 version
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("socketSeverNameGroup").version("socketSeverNameVersion").build();

        HelloService helloService = new HelloServiceImpl();

        SocketRpcServer  socketRpcServer = new SocketRpcServer();
        //注册接口到 zk中 的详细逻辑
        socketRpcServer.registerService(helloService, rpcServiceProperties);

    }


    /**
     * 客户端 发现已经注册到Zk上的接口
     * 最终的目的是ServiceDiscoveryImpl实现类的注册接口方法：lookupService
     * 入参为 String rpcServiceClassName   响应为  InetSocketAddress inetSocketAddress
     */
    @Test
    public  void clientDiscoveryZkTest() {
        ServiceDiscoveryImpl serviceDiscovery = new ServiceDiscoveryImpl();
        serviceDiscovery.lookupService("classNameJunit/name");

    }


//    CuratorFramework zkClient = CuratorUtils.getZkClient();
//        CuratorUtils.createPersistentNode(zkClient,"/rpc/classNameJunit");

    @Test
    public  void ServiceDiscoveryImplTest() {
        ServiceDiscoveryImpl serviceDiscovery = new ServiceDiscoveryImpl();
        serviceDiscovery.lookupService("classNameJunit/name");
    }



}
