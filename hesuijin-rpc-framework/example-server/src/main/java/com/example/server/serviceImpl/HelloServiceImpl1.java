package com.example.server.serviceImpl;

import com.example.api.Hello;
import com.example.api.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
@Slf4j
public class HelloServiceImpl1 implements HelloService {

    static {
        System.out.println("HelloServiceImpl被创建");
    }

    @Override
    public String helloServiceApi(Hello hello) {
        log.info("HelloServiceImpl收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl返回: {}.", result);
        return result;
    }

}
