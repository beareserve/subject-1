package com.study.netty.push.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

public class XNettyServer {
    public static void main(String[] args) throws Exception {
        // 1�� �̶߳���
        // accept �������ӵ��̳߳�
        EventLoopGroup acceptGroup = new NioEventLoopGroup();
        // read io �������ݵ��̳߳�
        EventLoopGroup readGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(acceptGroup, readGroup);
            // 2�� ѡ��TCPЭ�飬NIO��ʵ�ַ�ʽ
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    // 3�� ְ�������壨�����յ�����ô����
                    ChannelPipeline pipeline = ch.pipeline();
                    // TODO 3.1 ���ӽ�����
                    pipeline.addLast(new XDecoder());
                    // TODO 3.2 ��ӡ������ handdler
                    pipeline.addLast(new XHandller());
                }
            });
            // 4�� �󶨶˿�
            System.out.println("�����ɹ����˿� 9999");
            b.bind(9999).sync().channel().closeFuture().sync();
        } finally {
            acceptGroup.shutdownGracefully();
            readGroup.shutdownGracefully();
        }
    }
}
