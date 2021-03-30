package com.example.demo.zookeeperDemo;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/3/30
 */
@Slf4j
public class ZookeeperListenable {


    public void nodeListen(CuratorFramework zkClient) throws Exception {
        //监听 该节点 /node1
        String nodePath = "/node1";
        //cacheData的 true 和 false
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, nodePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            log.info("你好啊  我是{}的子节点，我刚刚进行了 {} 操作",nodePath,pathChildrenCacheEvent.getType());

            //开启针对某个节点的监听器 当这个节点的子节点发生变化  新增 删除 修改时 自定义操作
            if(PathChildrenCacheEvent.Type.CHILD_UPDATED .equals( pathChildrenCacheEvent.getType() )) {
               // do something
           }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }


//        //子节点增加
//        CHILD_ADDED,
//        //子节点更新
//        CHILD_UPDATED,
//        //子节点被删除
//        CHILD_REMOVED,
//
//        CONNECTION_SUSPENDED,
//        //很多时候报这个（？？？）
//        CONNECTION_RECONNECTED,
//        CONNECTION_LOST,
//
//        INITIALIZED;

}
