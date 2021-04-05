package com.example.client;

import com.example.api.Hello;
import com.example.api.HelloService;

/**
 * @Description:
 * @Author HeSuiJin
 * @Date 2021/4/5
 */
public class HelloController {

    private HelloService helloService;

    public void test() throws InterruptedException {
        String hello = this.helloService.helloServiceApi(new Hello("111", "222"));
        //如需使用 assert 断言，需要在 VM options 添加参数：-ea
        assert "Hello description is 222".equals(hello);

        Thread.sleep(12000);
        for (int i = 0; i < 10; i++) {
            System.out.println(helloService.helloServiceApi(new Hello("111", "222")));
        }
    }
}
