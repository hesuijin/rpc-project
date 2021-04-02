package com.example.demo.remoting.transport.socket;

import com.example.common.entity.RpcServiceProperties;
import com.example.common.exception.RpcException;
import com.example.demo.registry.zookeeper.ServiceDiscovery.ServiceDiscovery;
import com.example.demo.remoting.dto.RpcRequest;
import com.example.demo.remoting.transport.RpcRequestTransport;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Description:
 * Rpc框架Socket客户端  可参考rpc-project-demo的相关内容
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@AllArgsConstructor
@Slf4j
public class SocketRpcClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery;

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {

        //生成inetSocketAddress地址
        String rpcServiceName = RpcServiceProperties.builder().serviceName(rpcRequest.getInterfaceName())
                .group(rpcRequest.getGroup()).version(rpcRequest.getVersion()).build().toRpcServiceName();
        //使用Zookeeper注册
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcServiceName);

        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            //生成输出流
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //发出 请求
            objectOutputStream.writeObject(rpcRequest);

            //生成输入流
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            //获取 响应
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("调用服务失败:", e);
        }
    }
}
