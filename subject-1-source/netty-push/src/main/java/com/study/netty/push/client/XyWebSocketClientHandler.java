package com.study.netty.push.client;

import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;

public class XyWebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    public ChannelFuture getHandshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        handshakeFuture = ctx.newPromise();
    }

    static AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (handshaker == null) {
            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            URI uri = null;

            try {
                uri = new URI("ws://" + address.getHostString() + ":" + address.getPort() + "/websocket?userId=" + counter.incrementAndGet());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
        }
        handshaker.handshake(ctx.channel());
    }

    //    private Web
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                if ("true".equals(System.getProperty("netease.debug"))) ;
                System.out.println("WebSocket Client connected!");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e){
                if ("true".equals(System.getProperty("netease.debug"))) ;
                System.out.println("WebSocket Client failed to connected!");
                handshakeFuture.setFailure(e);
            }
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() +
                    ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            if ("true".equals(System.getProperty("netease.debug")));
            System.out.println("WebSocket Client received message:" + textFrame.text());
        } else if (frame instanceof PongWebSocketFrame) {
            if ("true".equals(System.getProperty("netease.debug")));
            System.out.println("WebSocket Client received pong");
        } else if (frame instanceof CloseWebSocketFrame) {
            if ("true".equals(System.getProperty("netease.debug")));
            System.out.println("WebSocket Client received closing");
            ch.close();
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if ("true".equals(System.getProperty("netease.debug")));
        System.out.println("WebSocket Client disconnect");
    }
}
