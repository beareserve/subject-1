package com.study.netty.push.handler;

import com.study.netty.push.test.TestCenter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.Map;

public class XyNewConnectHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        Map<String, List<String>> parameters = new QueryStringDecoder(req.uri()).parameters();

        String userId = parameters.get("userId").get(0);
        ctx.channel().attr(AttributeKey.valueOf("userId")).getAndSet(userId);
        TestCenter.saveConnection(userId, ctx.channel()); //±£¥Ê¡¨Ω”
    }
}
