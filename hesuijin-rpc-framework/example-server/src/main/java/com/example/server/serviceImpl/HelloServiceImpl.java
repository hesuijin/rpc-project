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
public class HelloServiceImpl implements HelloService {

    static {
        System.out.println("HelloServiceImpl 被创建");
    }

    @Override
    public String helloServiceApi(Hello hello) {
        log.info("HelloServiceImpl 收到 message: {} description:{}", hello.getMessage(),hello.getDescription());
        String result = "Hello client! 我是HelloServiceImpl回应  很高兴收到你的调用!";
        log.info("HelloServiceImpl 返回: {}.", result);
        return result;
    }

}
