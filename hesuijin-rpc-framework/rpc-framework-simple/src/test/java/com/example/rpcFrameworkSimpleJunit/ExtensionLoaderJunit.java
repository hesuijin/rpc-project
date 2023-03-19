package com.example.rpcFrameworkSimpleJunit;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.registryCenter.zookeeper.ServiceRegistry.ServiceRegistry;
import com.example.junitService.HelloExtensionLoaderService;
import com.example.common.extension.ExtensionLoader;
import com.example.junitService.HelloExtensionLoaderServiceExtends;
import com.example.junitService.HelloExtensionLoaderServiceParent;
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

    /**
     * 获取接口类的实现类
     */
    @Test
    public void extensionLoaderInterfaceTest() {
        // 获取加载类：后续用于获取 接口实现类(类的扩展类)
        ExtensionLoader<HelloExtensionLoaderService> extensionLoader = ExtensionLoader.getExtensionLoader(HelloExtensionLoaderService.class);

        //根据入参name获取对象实例  （这样获取的对象实例为单例 支持重复获取）
        HelloExtensionLoaderService helloExtensionLoader1 = extensionLoader.getExtensionInstance("helloExtensionLoaderKey1");
        HelloExtensionLoaderService helloExtensionLoader2 = extensionLoader.getExtensionInstance("helloExtensionLoaderKey2");
        HelloExtensionLoaderService helloExtensionLoader2Other = extensionLoader.getExtensionInstance("helloExtensionLoaderKey2");

        helloExtensionLoader1.helloExtensionLoaderMethod();
        helloExtensionLoader2.helloExtensionLoaderMethod();
        helloExtensionLoader2Other.helloExtensionLoaderMethod();

        log.info("helloExtensionLoader1 的对象信息"+helloExtensionLoader1);
        log.info("helloExtensionLoader2 的对象信息"+helloExtensionLoader2);
        log.info("helloExtensionLoader2Other 的对象信息"+helloExtensionLoader2Other);
    }


    /**
     * 获取父类的子类
     * 由于子类仅能使用重写了父类的方法，而不能是使用子类独有的方法，因此意义不大。
     *
     * 在ExtensionLoader中添加该逻辑，仅允许接口类获取其实现类。
     * if (!type.isInterface()) {
     *   throw new IllegalArgumentException("Extension type must be an interface.");
     * }
     */
    @Test
    @Deprecated
    public void extensionLoaderClassTest() {
        //父类与子类的方法并不是一一对应的
        //因此在哪怕获取到父类的子类 但仅能执行子类重写了父类的方法
        ExtensionLoader<HelloExtensionLoaderServiceParent> extensionLoaderClass = ExtensionLoader.getExtensionLoader(HelloExtensionLoaderServiceParent.class);
        HelloExtensionLoaderServiceParent extensionLoaderClassInstance = extensionLoaderClass.getExtensionInstance("helloExtensionLoaderKeyExtends");
        extensionLoaderClassInstance.helloExtensionLoaderMethod();
//        不能执行子类独有的方法HelloExtensionLoaderMethodOther
//        extensionLoaderClassInstance.HelloExtensionLoaderMethodOther();

    }
}
