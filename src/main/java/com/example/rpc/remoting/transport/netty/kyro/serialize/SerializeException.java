package com.example.rpc.remoting.transport.netty.kyro.serialize;


/**
 * @Description:
 * 序列化异常
 * @Author HeSuiJin
 * @Date 2021/3/28
 */
public class SerializeException extends RuntimeException {
    public SerializeException(String message) {
        super(message);
    }
}
