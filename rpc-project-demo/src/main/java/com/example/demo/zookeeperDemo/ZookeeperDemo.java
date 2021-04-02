package com.example.demo.zookeeperDemo;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/3/30
 */
@Slf4j
public class ZookeeperDemo {

    /**
     * 创建节点测试
     * @param zkClient
     * @throws Exception
     */
    public  void createTest(CuratorFramework zkClient) throws Exception {
        //创建节点
        // 注意:下面的代码会报错，因为还没创建 node1节点
        zkClient.create().forPath("/node1/00001");

        //解决方法1：
        //先创建 node1节点
        zkClient.create().forPath("/node1");

        //解决办法2:
        //creatingParentsIfNeeded() 可以保证父节点不存在的时候自动创建父节点
        zkClient.create().creatingParentsIfNeeded().forPath("/node1/00001");

        //创建持久化节点
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/node1/00001");
        //创建临时节点
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/00001");

        //创建节点是指定数据内容
        zkClient.create().creatingParentsIfNeeded().forPath("/node1/00001","java".getBytes());

    }

    /**
     * 获取 某节点下的所有子节点 测试
     * @param zkClient
     * @throws Exception
     */
    public void getChildNode(CuratorFramework zkClient) throws Exception {
        //获取某个节点的所有子节点路径
        List<String> childrenPaths = zkClient.getChildren().forPath("/node1");
        log.info("该节点的所有子节点：{}", JSONObject.toJSONString(childrenPaths));

    }

    /**
     *  //获取 某节点下的关联数据 测试
     * @param zkClient
     * @param nodePath
     * @throws Exception
     */
    public  void getDatabyNode(CuratorFramework zkClient,String nodePath) throws Exception {
        //获取节点的数据内容，获取到的是 byte数组
        byte[] bytes =  zkClient.getData().forPath(nodePath);
        //进行转换 byte 到 String的转换
        String  returnString = new String(bytes);
        log.info("返回节点 {} 的数据内容 ：{}",nodePath, returnString);
    }

    /**
     * 修改 某节点下的关联数据 测试
     * @param zkClient
     */
    public void setTest(CuratorFramework zkClient) throws Exception {
        getDatabyNode(zkClient,"/node1/00001");
        //更新节点数据内容
        zkClient.setData().forPath("/node1/00001","999".getBytes());
        getDatabyNode(zkClient,"/node1/00001");
    }

    /**
     * 删除节点测试
     * @param zkClient
     * @throws Exception
     */
    public void deleteTest(CuratorFramework zkClient) throws Exception {

        //删除单一节点
//        zkClient.delete().forPath("/node1/00005");

        //删除单一节点及其下所有节点
//        zkClient.delete().deletingChildrenIfNeeded().forPath("/node1");
    }

    /**
     * 判断节点是否存测试
     * @param zkClient
     * @throws Exception
     */
    public void exitTest(CuratorFramework zkClient) throws Exception {

        String nodePath =  "/node1/00005";

        //检查节点是否创建成功
        //不为null的话，说明节点创建成功
        boolean existFlag = zkClient.checkExists().forPath(nodePath) != null;
        if(existFlag){
            log.info("节点 {} 存在 ",nodePath);
        }else {
            log.info("节点 {} 不存在 ",nodePath);
        }
    }
}
