package com.example.rpcFrameworkSimpleJunit;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.registryCenter.zookeeper.ServiceRegistry.ServiceRegistry;
import com.example.junitService.HelloExtensionLoaderService;
import com.example.common.extension.ExtensionLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetSocketAddress;

/**
 * @Description: 配置读取类 单元测试
 * @Author HeSuiJin
 * @Date 2023/3/15
 */
@SpringBootTest
@Slf4j
public class ExtensionLoaderJunit {

    @Test
    public void extensionLoaderTest() {
        // 获取加载类：后续用于获取 接口实现类(类的扩展类)
        ExtensionLoader<HelloExtensionLoaderService> extensionLoader = ExtensionLoader.getExtensionLoader(HelloExtensionLoaderService.class);

        //根据入参name获取对象实例  （这样获取的对象实例为单例 支持重复获取）
        HelloExtensionLoaderService helloExtensionLoader1 = extensionLoader.getExtensionInstance("helloExtensionLoaderKey1");
        HelloExtensionLoaderService helloExtensionLoader2 = extensionLoader.getExtensionInstance("helloExtensionLoaderKey2");
        HelloExtensionLoaderService helloExtensionLoader2Other = extensionLoader.getExtensionInstance("helloExtensionLoaderKey2");

        helloExtensionLoader1.HelloExtensionLoaderMethod();
        helloExtensionLoader2.HelloExtensionLoaderMethod();
        helloExtensionLoader2Other.HelloExtensionLoaderMethod();

        log.info("helloExtensionLoader1 的对象信息"+helloExtensionLoader1);
        log.info("helloExtensionLoader2 的对象信息"+helloExtensionLoader2);
        log.info("helloExtensionLoader2Other 的对象信息"+helloExtensionLoader2Other);
    }

}
