package com.example.rpc.remoting.transport.proxy.cglibDynamicProxy;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/3/29
 */
public class Main {

    public static void main(String[] args) {
        AliSmsService aliSmsService = (AliSmsService) CglibProxyFactory.getProxy(AliSmsService.class);
        aliSmsService.send("java");
    }
}
