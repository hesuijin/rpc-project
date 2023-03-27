package com.example.client.clientStart;

import com.example.api.Hello;
import com.example.api.HelloService;
import com.example.demo.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 由于NettyClientMain 设置了@RpcScan(basePackage = {"com.example.client.clientStart"})
 * 因此HelloController会被扫描到，然后生成了对应的Spring对象helloController。
 * 由于helloController变为Spring对象,因此在SpringBeanPostProcessor会被执行初始化前后的特殊逻辑，
 * 最终helloService会被替换成对应的代理对象。
 * 代理对象内部以及包含了接口三要素信息，当执行调用时，会再取方法的相关信息，集成为RpcRequest。
 * @Author HeSuiJin
 * @Date 2023/3/28
 */
@Component
@Slf4j
public class HelloController {

    //RpcReference注解用于属性上
    @RpcReference( group = "nettyGroup" ,version = "nettyVersion")
    private HelloService helloService;

    public void test() throws InterruptedException {
        String helloResult = this.helloService.helloServiceApi(new Hello("测试netty发送信息", "测试netty"));
        log.info("调用helloServiceApi 获取响应"+helloResult);
        Thread.sleep(1000);
        for (int i = 1; i < 10; i++) {
            System.out.println(helloService.helloServiceApi(new Hello("测试netty发送信息,第" +i+"次！", "批量信息测试netty")));
        }
    }

    //如果没有手动设置代理类  无参构造构造函数 构造函数 均可以去掉 （仅是为了HelloController中兼容两种情况）
    //无参构造构造函数
    public HelloController(){

    }

    //构造函数
    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

}



