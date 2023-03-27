package com.example.demo.annotation;

import com.example.demo.spring.CustomScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Description:
 * 扫描包注解  指定需要扫描哪些包
 * Spring项目扫描到 @interface 注解类，然后利用@Import(CustomScannerRegistrar.class)，去导入CustomScannerRegistrar类到Spring容器中。
 * @Author HeSuiJin
 * @Date 2021/4/7
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomScannerRegistrar.class)
@Documented
public @interface RpcScan {

    String[] basePackage();
}
