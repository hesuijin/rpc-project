package com.example.demo.annotation;

import java.lang.annotation.*;

/**
 * @Description:
 * 客户端使用
 *  RPC定义注解(后续可以被扫描)  用于获取代理对象（用于属性上)
 *  该注解使用在客户端的接口上可以获取到接口信息，最终形成接口三要素：
 *  接口信息(接口位置 接口类名) - 接口所在组 -接口版本号
 *  @Inherited 可以被继承
 *
 *  * @Author HeSuiJin
 * @Date 2021/4/7
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {

    /**
     * 服务接口 版本号 默认值为空字符串
     */
    String version() default "";

    /**
     * 服务接口 分组  默认值为空字符串
     */
    String group() default "";
}
