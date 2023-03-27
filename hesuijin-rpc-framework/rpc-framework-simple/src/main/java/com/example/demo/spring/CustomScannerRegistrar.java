package com.example.demo.spring;

import com.example.demo.annotation.RpcScan;
import com.example.demo.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @Description:
 * CustomScannerRegistrar的作用是去扫描指定的类，使其存放到Spring容器中。
 *
 * 而CustomScannerRegistrar类本身，是依赖于@interface RpcScan中的@Import(CustomScannerRegistrar.class)，
 * 从而被导入到Spring容器中。
 *
 * 扫描自定义注解
 *  1：获取RpcScan注解中设置的路径（值）。
 *  2：扫描获取到的路径（值）下中指定的类注解，设置该类对象为Spring对象。
 * @Author HeSuiJin
 * @Date 2023/3/7
 */
@Slf4j
public class CustomScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    //包扫描（扫描本包下的注解）
    private static final String SPRING_BEAN_BASE_PACKAGE = "com.example.demo.spring";
    //RpcScan注解的key  （通过该key获取路径值）
    private static final String BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";
    //加载资源
    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

    }

    /**
     * registerBeanDefinitions 注册对象到Spring容器中
     * 从指定的 DOM 文档中读取 bean 定义并将其注册到 bean 定义表中
     *  通过registerBeanDefinitions()方法，我们可以向Spring容器中注册bean实例。
     *  Spring官方在动态注册bean时，大部分套路其实是使用ImportBeanDefinitionRegistrar接口。
     * @param annotationMetadata
     * @param beanDefinitionRegistry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        //get the attributes and values ​​of RpcScan annotation
        //扫描出添加了RpcScan.class的类
        AnnotationAttributes rpcScanAnnotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RpcScan.class.getName()));
        String[] rpcScanBasePackages = new String[0];
        if (rpcScanAnnotationAttributes != null) {
            //通过RpcScan注解设置的路径（值）  的集合  "com.example.demo.remotingCenter.transport.netty","com.example.server.serviceImpl
            rpcScanBasePackages = rpcScanAnnotationAttributes.getStringArray(BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        if (rpcScanBasePackages.length == 0) {
            rpcScanBasePackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }

        //注意在 ClassPathBeanDefinitionScanner 的父类ClassPathScanningCandidateComponentProvider的逻辑中，存在有registerDefaultFilters方法
        //实际上Component注解已经是默认加入被扫描的注解了
        // protected void registerDefaultFilters() {
        //		this.includeFilters.add(new AnnotationTypeFilter(Component.class));
        //}

        // Scan the RpcService annotation （扫描类型添加 自定义RpcService注解扫描）
        CustomScanner rpcServiceScanner = new CustomScanner(beanDefinitionRegistry, RpcService.class);
        // Scan the Component annotation  （扫描类型添加 Component注解扫描）
        CustomScanner componentBeanScanner = new CustomScanner(beanDefinitionRegistry, Component.class);
        //resourceLoader（资源读取 需要设置使用该对象） "org.springframework.context.annotation.AnnotationConfigApplicationContext@7d0587f1"
        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
            componentBeanScanner.setResourceLoader(resourceLoader);
        }

        //springBeanScanner.scan("需要扫描的路径") 里面源码实现使用了doscan
        //实际上使用springBeanScanner.findCandidateComponents(basePackage) 来添加了相关注解的类  能被扫描为Spring对象

        //仅扫描com.example.demo.spring下的Component注解   目的是使SpringBeanPostProcessor（添加了Component注解）也能被扫描为Spring对象
        int springBeanAmount = componentBeanScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("springBeanScanner扫描的数量 [{}]", springBeanAmount);

        Set<BeanDefinition>  springBeanDefinitions = componentBeanScanner.findCandidateComponents(SPRING_BEAN_BASE_PACKAGE);
        if (!springBeanDefinitions.isEmpty()) {
            for (BeanDefinition beanDefinition :springBeanDefinitions){
                log.info("springBeanScanner扫描的类:[{}]",beanDefinition.getBeanClassName());
            }
        }

        //流程如下：
        //RpcScan注解中设置的basePackage的路径（值）下的资源   该路径（值）下添加了相关注解的类 就能被扫描为Spring对象
        //@RpcScan(basePackage = {"com.example.demo.remotingCenter.transport.netty","com.example.server.serviceImpl"})
        //扫描@RpcScan注解获取路径（值）： com.example.demo.remotingCenter.transport.netty  以及 com.example.server.serviceImpl

        //rpcServiceScanner仅扫描路径（值）下的以下注解： RpcService注解+默认添加的Component注解
        //扫描com.example.demo.remotingCenter.transport.netty路径（值） 获取到 Spring对象  NettyRpcServer(@Component)->nettyRpcServer
        //扫描com.example.server.serviceImpl路径（值）                  获取到 Spring对象  HelloServiceNettyImpl(@RpcService)-》helloServiceNettyImpl
        int rpcServiceCount = rpcServiceScanner.scan(rpcScanBasePackages);
        log.info("rpcServiceScanner扫描的数量 [{}]", rpcServiceCount);

        for (String rpcScanBasePackage : rpcScanBasePackages){
            Set<BeanDefinition>  rpcBeanDefinitions = rpcServiceScanner.findCandidateComponents(rpcScanBasePackage);
            if (!rpcBeanDefinitions.isEmpty()) {
                for (BeanDefinition beanDefinition :rpcBeanDefinitions){
                    log.info("rpcServiceScanner扫描的类:[{}]",beanDefinition.getBeanClassName());
                }
            }
        }

    }
}
