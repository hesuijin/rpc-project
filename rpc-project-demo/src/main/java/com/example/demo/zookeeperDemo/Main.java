package com.example.demo.zookeeperDemo;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/3/30
 */
@Slf4j
public class Main {
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;

    public static void main(String[] args)  {

        // Retry strategy. Retry 3 times, and will increase the sleep time between retries.
        //创建连接 Zookeeper的实体
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                // the server to connect to (can be a server list)
                .connectString("47.113.101.241:2181")
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();

        //执行监听操作
        ZookeeperListenable zookeeperListenable = new ZookeeperListenable();
        try {
            zookeeperListenable.nodeListen(zkClient);
        } catch (Exception e) {
            log.info("Zookeeper 测试 监听器异常 "+e.getMessage() ,e);
        }

        ZookeeperDemo zookeeperDemo = new ZookeeperDemo();

        try {
            //创建测试
//            zookeeperDemo.createTest(zkClient);

            //获取 某节点下的所有子节点 测试
            zookeeperDemo.getChildNode(zkClient);
            //获取 某节点下的关联数据 测试
//            zookeeperDemo.getDatabyNode(zkClient,"/node1");

            //修改 某节点下的关联数据 测试
//            zookeeperDemo.setTest(zkClient);

            //删除测试
//            zookeeperDemo.deleteTest(zkClient);

            //判断节点是否存在
//            zookeeperDemo.exitTest(zkClient);
        } catch (Exception e) {
            log.info("Zookeeper 测试常用方法 异常 "+e.getMessage() ,e);
        }




    }

}
