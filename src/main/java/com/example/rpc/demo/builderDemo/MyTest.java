package com.example.rpc.demo.builderDemo;

/**
 * @Author HSJ
 * @Date 2021/1/7 0:32
 * @Description:
 */
public class MyTest {


    public String demo(String myName){
     return "Hello World" + myName;
    }

    public static void main(String[] args) {

        //新建
        UserInfo userInfo = UserInfo.builder()
                .name("HeSuiJin")
                .address("GZ")
                .build();
        //修改
        userInfo = userInfo.toBuilder()
                .name("何穗金")
                .address("广州")
                .build();

        System.out.println(userInfo);



    }
}
