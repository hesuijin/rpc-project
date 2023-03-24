package com.example.rpcFrameworkSimpleJunit;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.registryCenter.zookeeper.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2023/3/13
 */
@SpringBootTest
@Slf4j
public class CuratorUtilsJunit {

    CuratorFramework zkClient = null;

    /**
     * 创建Zk客户端(实际上该客户端直接连接Zk的服务端)
     */
    @Before
    public  void CuratorUtilBeforeTest() {
        //获取ZkClient客户端(实际上该客户端直接连接Zk的服务端)
         zkClient = CuratorUtils.getZkClient();
    }

    /**
     * 测试关于Zookeeper的  工具类CuratorUtils
     */
    @Test
    public void CuratorUtilsTest() {

        //获取ZkClient客户端(实际上该客户端直接连接Zk的服务端)
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        String path1 = "/my-rpc"+"/serviceInfo"+"/127.0.0.1:8080";
        String path2 = "/my-rpc"+"/serviceInfo"+"/127.0.0.2:8080";

        CuratorUtils.createPersistentNode(zkClient,path1);
        CuratorUtils.createPersistentNode(zkClient,path2);

        //获取子节点  /my-rpc是代码里面拼接上的
        List<String> childrenNodes = CuratorUtils.getChildrenNodesAndFirstRegisterWatcher(zkClient,"serviceInfo");

        log.info("打印子节点:{}", JSONObject.toJSONString(childrenNodes));
        String path3 = "/my-rpc"+"/serviceInfo"+"/127.0.0.3:8080";
        CuratorUtils.createPersistentNode(zkClient,path3);
    }

    /**
     * 清除Zk中  对应服务器已经注册到Zk中的节点
     */
//    @After
    public  void CuratorUtilsAfterTest() {
        String  path = "/my-rpc";
        String  pathServiceInfo = "/my-rpc/serviceInfo";

        log.info("删除前 查询+++++++");
        getChildrenTest(path);             //打印 该节点:/my-rpc 的所有子节点：["serviceInfo"]
        getChildrenTest(pathServiceInfo);  //打印 该节点:/my-rpc/serviceInfo 的所有子节点：["127.0.0.1:8080","127.0.0.3:8080","127.0.0.2:8080"]
        log.info("删除前 查询+++++++");

        InetSocketAddress inetSocketAddress1 = new InetSocketAddress("127.0.0.1", 8080);
        InetSocketAddress inetSocketAddress2 = new InetSocketAddress("127.0.0.2", 8080);
        InetSocketAddress inetSocketAddress3 = new InetSocketAddress("127.0.0.3", 8080);

        CuratorUtils.clearRegistry(zkClient,inetSocketAddress1);
        CuratorUtils.clearRegistry(zkClient,inetSocketAddress2);
        CuratorUtils.clearRegistry(zkClient,inetSocketAddress3);

        log.info("删除 inetSocketAddress后 查询+++++++");
        getChildrenTest(path);            //打印 该节点:/my-rpc 的所有子节点：["serviceInfo"]
        getChildrenTest(pathServiceInfo); //打印 该节点:/my-rpc/serviceInfo 的所有子节点：[]
        log.info("删除 inetSocketAddress后 查询+++++++");


        try {
            zkClient.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            log.error("删除单一节点：{} 及其下所有节点异常：{}",path,e.getMessage());
        }

        log.info("删除 my-rpc后 查询+++++++");
        getChildrenTest(path);             //打印 该节点:/my-rpc 的所有子节点：[]
        getChildrenTest(pathServiceInfo);  //打印 该节点:/my-rpc/serviceInfo 的所有子节点：[]
        log.info("删除 my-rpc后 查询+++++++");

    }

    /**
     * 获取节点下的子节点
     * @param path
     * @return
     */
    private List<String> getChildrenTest(String path){
        List<String>  childrenPaths = new ArrayList<>();
        try {
            childrenPaths = zkClient.getChildren().forPath(path);
        } catch (Exception e) {
            log.error("获取单一节点：{}及其下所有节点异常：{}",path,e.getMessage());
        }

        log.info("该节点:{} 的所有子节点：{}", path,JSONObject.toJSONString(childrenPaths));
        return childrenPaths;
    }

    @Test
    public void getChildrenByPathTest() throws Exception {
        String path = "/my-rpc";

        //删除单一节点及其下所有节点
        zkClient.delete().deletingChildrenIfNeeded().forPath(path);

//        String path = "/my-rpc/com.example.api.HelloServicesocketSeverNameGroupsocketSeverNameVersion";


        List<String>  childrenPaths = new ArrayList<>();
        try {
            childrenPaths = zkClient.getChildren().forPath(path);
        } catch (Exception e) {
            log.error("获取单一节点：{}及其下所有节点异常：{}",path,e.getMessage());
        }

        log.info("该节点:{} 的所有子节点：{}", path,JSONObject.toJSONString(childrenPaths));
    }
}
