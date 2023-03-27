package com.example.demo.annotation;

import java.lang.annotation.*;

/**
 * @Description:
 * 服务端使用
 *  RPC自定义注解(后续可以被扫描) 用于服务接口注册
 *  该注解使用在服务端的接口上可以获取到接口信息，最终形成接口三要素：
 *  接口信息(接口位置 接口类名) - 接口所在组 -接口版本号
 *  @Inherited 可以被继承
 *
 * @Author HeSuiJin
 * @Date 2023/3/27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {

    /**
     * 服务接口 版本号 默认值为空字符串
     */
    String version() default "";

    /**
     * 服务接口 分组  默认值为空字符串
     */
    String group() default "";
}
