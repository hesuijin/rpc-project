package com.example.rpc.demo;

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
//        UserInfo userInfo1 = new UserInfo();

//
//        userInfo1 = userInfo1.toBuilder().email("fafaf").build();
//        System.out.println(userInfo1);

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
