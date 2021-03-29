package com.example.rpc.remoting.transport.proxy.jdkDynamicProxy;

import com.example.rpc.remoting.transport.proxy.jdkDynamicProxy.SmsService;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/3/29
 */
public class SmsServiceImpl implements SmsService{

    @Override
    public String send(String message) {
        System.out.println("send message:" + message);
        return message;
    }
}
