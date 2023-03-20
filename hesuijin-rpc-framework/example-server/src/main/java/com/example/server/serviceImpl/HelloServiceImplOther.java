package com.example.server.serviceImpl;

import com.example.api.Hello;
import com.example.api.HelloService;
import com.example.demo.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
@Slf4j
//@RpcService(group = "socketSeverNameGroup", version = "socketSeverNameVersion")
public class HelloServiceImplOther implements HelloService {

    static {
        System.out.println("HelloServiceImplOther 被创建");
    }

    @Override
    public String helloServiceApi(Hello hello) {
        log.info("HelloServiceImplOther 收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImplOther 返回: {}.", result);
        return result;
    }

}
