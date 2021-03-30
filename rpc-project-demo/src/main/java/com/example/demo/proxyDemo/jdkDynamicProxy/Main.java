package com.example.demo.proxyDemo.jdkDynamicProxy;


/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/3/29
 */
public class Main {

    public static void main(String[] args) {
        SmsService smsService = new SmsServiceImpl();
        SmsService smsServiceImplProxy = (SmsService) JdkProxyFactory.getProxy(smsService);
        smsServiceImplProxy.send("java");
    }
}
