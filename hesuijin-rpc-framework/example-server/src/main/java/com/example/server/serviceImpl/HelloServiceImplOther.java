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
public class HelloServiceImplOther implements HelloService {

    static {
        System.out.println("HelloServiceImplOther 被创建");
    }

    @Override
    public String helloServiceApi(Hello hello) {
        log.info("HelloServiceImplOther 收到 message: {} description:{}", hello.getMessage(),hello.getDescription());
        String result = "Hello client! 我是HelloServiceImplOther回应  很高兴收到你的调用!";
        log.info("HelloServiceImplOther 返回: {}.", result);
        return result;
    }

}
