package com.example.demo.proxyDemo.cglibDynamicProxy;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/3/29
 */
public class AliSmsService {
    public String send(String message) {
        System.out.println("send message:" + message);
        return message;
    }
}
