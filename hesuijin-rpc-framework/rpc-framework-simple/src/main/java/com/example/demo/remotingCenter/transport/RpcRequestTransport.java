package com.example.demo.remotingCenter.transport;

import com.example.common.extension.SPI;
import com.example.demo.remotingCenter.dto.RpcRequest;

/**
 * @Description:
 * 传输服务请求接口
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@SPI
public interface RpcRequestTransport {

    Object sendRpcRequest(RpcRequest rpcRequest);
}
