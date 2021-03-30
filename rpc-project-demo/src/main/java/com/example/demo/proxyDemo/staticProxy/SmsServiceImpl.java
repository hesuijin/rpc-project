package com.example.demo.proxyDemo.staticProxy;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/3/29
 */
public class SmsServiceImpl implements SmsService {

    @Override
    public String send(String message) {
        System.out.println("send message:" + message);
        return message;
    }
}
