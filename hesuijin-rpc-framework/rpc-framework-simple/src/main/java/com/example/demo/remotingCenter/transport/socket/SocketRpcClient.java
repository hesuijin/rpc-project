package com.example.demo.remotingCenter.transport.socket;

import com.example.common.entity.RpcServiceProperties;
import com.example.common.exception.RpcException;
import com.example.common.extension.ExtensionLoader;
import com.example.demo.registryCenter.zookeeper.ServiceDiscovery.ServiceDiscovery;
import com.example.demo.remotingCenter.dto.RpcRequest;
import com.example.demo.remotingCenter.transport.RpcRequestTransport;
import com.sun.xml.internal.bind.v2.TODO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Description:
 * Rpc框架Socket客户端
 * 连接Socket服务端
 * 1：获取ObjectOutputStream输出流
 * 2：写入请求数据到输出流中
 * 3：获取ObjectInputStream输入流
 * 4：从输入流中读取响应结果
 *
 *     try-with-resource语句:
 *         try括号中包含资源声明了一种或多种资源，并且保证每个声明了的资源在语句结束时都会被自动关闭，
 *         那么如何定义资源，任何实现了java.lang.AutoCloseable（Closeable）接口的对象，都可以在try-with-resource语句中使用，
 *         最终在语句结束时会把资源全部关闭掉。
 *
 *         如： try (Socket socket = new Socket(host, port))   中最后会自动执行 socket.close();
 *         输出流 输入流也是类似：
 *         objectOutputStream.close();  objectInputStream.close();
 *
 *     objectOutputStream.flush():
 *         部分outputstream的子类实现了缓存机制，为了提高效率,当write()的时候不一定直接发过去，有可能先缓存起来一起发出去,
 *         flush()的作用就是强制性地将缓存中的数据发出去.
 *
 * @Author HeSuiJin
 * @Date 2021/4/2
 */
@AllArgsConstructor
@Slf4j
public class SocketRpcClient implements RpcRequestTransport {

    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient() {
        ExtensionLoader<ServiceDiscovery> extensionLoader =  ExtensionLoader.getExtensionLoader(ServiceDiscovery.class);
        this.serviceDiscovery = extensionLoader.getExtensionInstance("zk");
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        //获取inetSocketAddress
        InetSocketAddress inetSocketAddress = getInetSocketAddress(rpcRequest);
        //自动关闭资源
        try (Socket socket = new Socket()) {
            socket.connect(inetSocketAddress);
            //生成输出流
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //发出 请求
            objectOutputStream.writeObject(rpcRequest);
            // flush()的作用就是强制性地将缓存中的数据发出去.
            objectOutputStream.flush();
            //生成输入流
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            //获取 响应
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("调用服务失败:", e);
        }
    }

    /**
     * 根据rpcRequest获取 inetSocketAddress
     * @param rpcRequest
     * @return
     */
    public  InetSocketAddress getInetSocketAddress(RpcRequest rpcRequest){
        // rpcServiceClassName(接口三要素素) ：
        // 包含className（interfaceName）服务接口类名称 com.example.demo.HelloService 以及 group + version
        String rpcServiceClassName = RpcServiceProperties.builder().serviceName(rpcRequest.getInterfaceName())
                .group(rpcRequest.getGroup()).version(rpcRequest.getVersion()).build().toRpcServiceName();
        //使用Zookeeper发现
        //获取请求的Socket服务端  （该服务端有对应的请求接口）
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcServiceClassName);

        return inetSocketAddress;
    }
}
