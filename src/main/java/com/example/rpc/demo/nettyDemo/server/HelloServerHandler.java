package com.example.rpc.demo.nettyDemo.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * @Description: 自定义服务端 ChannelHandler 处理消息
 * @Author HeSuiJin
 * @Date 2021/3/29
 */

@ChannelHandler.Sharable
@Slf4j
public class HelloServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext,Object msg){
        try {
            ByteBuf in = (ByteBuf) msg;
            System.out.println("message from client:" + in.toString(CharsetUtil.UTF_8));
            // 发送消息给客户端
            channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("你也好！", CharsetUtil.UTF_8));
        }catch (Exception e){
            log.info("服务端消息处理异常："+e.getMessage(),e);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        channelHandlerContext.close();
    }

}
