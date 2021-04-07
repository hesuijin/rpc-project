package com.example.demo.annotation;

/**
 * @Description:
 * RPC 服务接口 自定义注解
 * @Author HeSuiJin
 * @Date 2021/4/7
 */
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
