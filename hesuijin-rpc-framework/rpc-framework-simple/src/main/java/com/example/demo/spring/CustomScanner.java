package com.example.demo.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * @Description:
 * CustomScanner继承了ClassPathBeanDefinitionScanner
 * ClassPathBeanDefinitionScanner作用：
 * 将指定包下的类通过一定规则过滤后，将Class信息包装成BeanDefinition的形式注册到IOC容器中。
 * @Author HeSuiJin
 * @Date 2021/4/7
 */
public class CustomScanner extends ClassPathBeanDefinitionScanner {

    /**
     * @param registry
     * @param annoType
     */
    public CustomScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annoType) {
        //Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory.
        //可以认为是默认构造函数
        super(registry);
        //Add an include type filter to the <i>end</i> of the inclusion list.
        //过滤规则：添加了指定了入参的注解类型
        super.addIncludeFilter(new AnnotationTypeFilter(annoType));
    }

    /**
     * 对指定路径（值）的指定注解 （addIncludeFilter设置的注解） 进行扫描
     * @param basePackages
     * @return
     */
    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }

}
