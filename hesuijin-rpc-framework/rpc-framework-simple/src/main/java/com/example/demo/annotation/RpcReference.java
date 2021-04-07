package com.example.demo.annotation;

import java.lang.annotation.*;

/**
 * @Description:
 * RPC 引用 自定义注解
 * @Author HeSuiJin
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
