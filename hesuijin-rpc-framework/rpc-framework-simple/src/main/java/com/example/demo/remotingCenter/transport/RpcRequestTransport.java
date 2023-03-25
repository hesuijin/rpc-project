package com.example.demo.remotingCenter.transport;

import com.example.common.extension.SPI;
import com.example.demo.remotingCenter.dto.RpcRequest;

/**
 * @Description:
 * 请求传输接口类
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@SPI
public interface RpcRequestTransport {

    Object sendRpcRequest(RpcRequest rpcRequest);
}
