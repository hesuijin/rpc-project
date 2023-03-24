package com.example.common.enums;

import lombok.Data;

/**
 * @Description: PRC常量
 * @Author HeSuiJin
 * @Date 2023/3/22
 */
@Data
public class RpcConstant {

    public static class  SocketRpcServer{
        //Socket服务端 所绑定的端口号
        public static final Integer PORT = 9998;

    }
}
