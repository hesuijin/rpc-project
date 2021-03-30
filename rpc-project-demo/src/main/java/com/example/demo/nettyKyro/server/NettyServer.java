package com.example.demo.nettyKyro.server;

import com.example.demo.nettyKyro.codec.NettyKryoDecoder;
import com.example.demo.nettyKyro.codec.NettyKryoEncoder;
import com.example.demo.nettyKyro.dto.*;
import com.example.demo.nettyKyro.serialize.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @Description:
 * Netty服务端
 * NettyServer 主要用于开启一个服务端并接收客户端的请求并处理
 * @Author HeSuiJin
 * @Date 2021/3/29
 */
public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void run() {

        // 1.bossGroup 用于接收连接，workerGroup 用于具体的处理
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        KryoSerializer kryoSerializer = new KryoSerializer();
        try {

            //2.创建服务端启动引导
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            //3.给引导类配置两大线程组,确定了线程模型
            serverBootstrap.group(bossGroup, workerGroup)

                    // 4.指定 IO 模型
                    .channel(NioServerSocketChannel.class)

                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))

                    //5.可以自定义客户端消息的业务处理逻辑
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcRequest.class));
                            ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcResponse.class));
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            // 6.绑定端口,调用 sync 方法阻塞知道绑定完成
            ChannelFuture f = serverBootstrap.bind(port).sync();

            // 7.等待连接关闭（阻塞，直到Channel关闭）
            // closeFuture()方法获取Channel 的CloseFuture对象,然后调用sync()方法
            Channel futureChannel = f.channel();
            futureChannel.closeFuture().sync();

        } catch (InterruptedException e) {
            logger.error("occur exception when start server:", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }



}
