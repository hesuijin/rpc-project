package com.example.demo.spring;

import com.example.common.entity.RpcServiceProperties;
import com.example.common.enums.RpcConstant;
import com.example.common.extension.ExtensionLoader;
import com.example.common.factory.SingletonFactory;
import com.example.demo.annotation.RpcReference;
import com.example.demo.annotation.RpcService;
import com.example.demo.provider.ServiceProvider;
import com.example.demo.provider.ServiceProviderImpl;
import com.example.demo.proxy.RpcClientProxy;
import com.example.demo.remotingCenter.transport.RpcRequestTransport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * @Description:
 * 通过CustomScannerRegistrar把SpringBeanPostProcesso扫描为Spring对象
 * 该SpringBeanPostProcessor作为Spring对象，通过实现BeanPostProcessor类，
 * 完成Spring对象初始化前后的特殊操作：
 *      postProcessBeforeInitialization：服务端接口类进行节点注册
 *      postProcessAfterInitialization：客户端接口类对应的实例对象进行代理
 *
 * 扩展知识Spring的生命周期;
 *     1：注册阶段
 *     2：实例化阶段
 *     3：初始化阶段
 *     4：销毁阶段
 * InstantiationAwareBeanPostProcessor 为实例化的操作：获取一个新的Spring对象（内存中开辟空间）
 * BeanPostProcessor 为初始化的操作：Spring对象的属性赋予值
 * 更详细内容请到Spring的相关章节学习
 * @Author HeSuiJin
 * @Date 2023/3/37
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    //获取netty单例实例对象
    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
        //客户端的rpcClient默认创建 （如果是客户端启动调用 需要根据rpcClient创建代理对象）
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtensionInstance("netty");
    }

    /**
     *  Spring bean在实例化之后  BeanPostProcessor在初始化之前会调用  postProcessBeforeInitialization
     * 对添加了RpcService注解的 服务端接口类对应的实现类  进行节点注册
     *
     * 1: org.springframework.beans.factory.support.DefaultListableBeanFactory - Creating shared instance of singleton bean 'helloServiceNettyImpl'
     * 2: HelloServiceNettyImpl 被创建
     * 3: com.example.demo.spring.SpringBeanPostProcessor - [com.example.server.serviceImpl.HelloServiceNettyImpl] is annotated with  [com.example.demo.annotation.RpcService]
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    //@SneakyThrows  等同于把该注解下代码生成一个try...catch块，并把异常向上抛出来。
    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //判断RpcService注解
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            // get RpcService annotation
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // build RpcServiceProperties
            RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                    .group(rpcService.group()).version(rpcService.version()).build();
            //节点注册 (netty)
            serviceProvider.publishService(bean, rpcServiceProperties, RpcConstant.NettyRpcServer.PORT);
        }
        return bean;
    }

    /**
     * Spring bean在实例化之后  BeanPostProcessor在初始化之后会调用  postProcessAfterInitialization
     * 对添加了RpcReference注解的 客户端接口类对应的实例对象  进行代理
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        //获取targetClass下的全部属性
        Field[] declaredFields = targetClass.getDeclaredFields();
        //扫描自定义注解RpcReference
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                        .group(rpcReference.group()).version(rpcReference.version()).build();
                //获取代理对象（Netty的代理对象）
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceProperties);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return bean;
    }
}
