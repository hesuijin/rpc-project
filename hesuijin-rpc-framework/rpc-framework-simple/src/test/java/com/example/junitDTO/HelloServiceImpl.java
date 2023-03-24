package com.example.junitDTO;

import com.example.demo.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
@Slf4j
@RpcService(group = "socketSeverNameGroup", version = "socketSeverNameVersion")
public class HelloServiceImpl implements HelloService {

    static {
        System.out.println("HelloServiceImpl2 被创建");
    }

    @Override
    public String helloServiceApi(Hello hello) {
        log.info("HelloServiceImpl2 收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        log.info("HelloServiceImpl2 返回: {}.", result);
        return result;
    }

}
