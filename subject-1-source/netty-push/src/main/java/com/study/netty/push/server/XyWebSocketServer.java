package com.study.netty.push.server;

import com.study.netty.push.test.TestCenter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class XyWebSocketServer {

    private static int PORT = 9000;

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
        ServerBootstrap b= new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childHandler(new XyWebSocketServerInitializer())
                    .childOption(ChannelOption.SO_REUSEADDR, true);
            for (int i = 0; i < 100; i++) {
                b.bind(PORT++).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
//                        if ("true".equals(System.getProperty("netease.debug"))) {
                            System.out.println("端口绑定完成：" + future.channel().localAddress());
//                        }
                    }
                });
            }
            TestCenter.startTest();
            System.in.read();
        } catch (Exception e) {

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
