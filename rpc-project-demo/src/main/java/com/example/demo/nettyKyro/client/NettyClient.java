package com.example.demo.nettyKyro.client;

import com.example.demo.nettyKyro.dto.*;
import com.example.demo.nettyKyro.codec.NettyKryoDecoder;
import com.example.demo.nettyKyro.codec.NettyKryoEncoder;
import com.example.demo.nettyKyro.serialize.KryoSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @Description:
 * Netty客户端
 * 主要有一个用于向服务端发送消息的sendMessage（）方法
 * 通过该方法可以将消息也就是RpcRequest对象发送到服务端
 * 并且可以同步获取服务端返回的结果也就是RpcResponse对象
 * @Author HeSuiJin
 * @Date 2021/3/29
 */
@Slf4j
public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private final String host;
    private final int port;
    private static final Bootstrap bootStrap;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // 初始化相关资源比如 EventLoopGroup, Bootstrap
    static {
        //1.创建一个 NioEventLoopGroup 对象实例
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        //2.创建客户端启动引导类：Bootstrap
        bootStrap = new Bootstrap();

        KryoSerializer kryoSerializer = new KryoSerializer();

        //3.指定线程组
        bootStrap.group(eventLoopGroup)

                //4.指定 IO 模型
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                //  如果 15 秒之内没有发送数据给服务端的话，就发送一次心跳请求
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)

                // 5.这里可以自定义消息的业务处理逻辑
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        /*
                         自定义序列化编解码器
                         */
                        // RpcResponse -> ByteBuf
                        ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                        // ByteBuf -> RpcRequest
                        ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    /**
     * 发送消息到服务端
     *
     * @param rpcRequest 消息体
     * @return 服务端返回的数据
     */
    public RpcResponse sendMessage(RpcRequest rpcRequest) {
        try {

            // bootStrap = new Bootstrap(); 的相关参数以及逻辑已经初始化了
            // 6.尝试建立连接
            ChannelFuture f = bootStrap.connect(host, port).sync();

            logger.info("client connect  {}", host + ":" + port);
            Channel futureChannel = f.channel();
            logger.info("send message");
            if (futureChannel != null) {
                futureChannel.writeAndFlush(rpcRequest).addListener(future -> {
                    if (future.isSuccess()) {
                        logger.info("client send message: [{}]", rpcRequest.toString());
                    } else {
                        logger.error("Send failed:", future.cause());
                    }
                });

                // 7.等待连接关闭（阻塞，直到Channel关闭）
                futureChannel.closeFuture().sync();

                //Channel继承了AttributeMap 也就是Channel具有AttributeMap的相关属性
                // 将服务端返回的数据也就是RpcResponse对象取出
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return futureChannel.attr(key).get();
            }
        } catch (InterruptedException e) {
            logger.error("occur exception when connect server:", e);
        }
        return null;
    }


}