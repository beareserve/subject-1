package com.study.netty.push.server;

import com.study.netty.push.handler.NewConnectHandler;
import com.study.netty.push.handler.WebSocketServerHandler;
import com.study.netty.push.handler.XyNewConnectHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class XyWebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec()); //http������������httpЭ�����
        pipeline.addLast(new HttpObjectAggregator(65536)); //
        pipeline.addLast(new WebSocketServerHandler()); //����socket����
        pipeline.addLast(new XyNewConnectHandler());
    }
}
