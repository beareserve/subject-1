package com.study.hc.net.nio.xy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class NIOServerV2 {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open(); //绑定监听端口的通道对象，通过accept()方法获得具体通道连接
        channel.configureBlocking(false);

        Selector selector = Selector.open();
        SelectionKey selectionKey = channel.register(selector, 0, channel); //将serverSocketChannel注册到selector
        selectionKey.interestOps(SelectionKey.OP_ACCEPT); //对serverSocketChannel上面的accept事件感兴趣（serverSocketChannel只支持accept操作）

//        channel.bind(new InetSocketAddress(8080));
        channel.socket().bind(new InetSocketAddress(8080));
        System.out.println("启动成功");
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectionKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();

                //处理连接事件
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.attachment();
                    SocketChannel ch = server.accept();
                    ch.configureBlocking(false);
                    ch.register(selector, SelectionKey.OP_READ, ch); //将拿到的客户端连接通道，注册到selector上面
                    System.out.println("收到新连接：" + ch.getRemoteAddress());
                }

                //处理消息接收事件
                if (key.isReadable()) {
                    SocketChannel ch = (SocketChannel) key.attachment();
                    try {
                        ByteBuffer clientBuffer = ByteBuffer.allocateDirect(1024);
                        while (ch.isOpen() && ch.read(clientBuffer) != -1) { //不判断isOpen，后面操作socketChannel会报错连接不存在
                            if (clientBuffer.position() > 0) break;
                        }

                        if (clientBuffer.position() == 0) continue; //没数据了，不进行后面的处理（但啥情况下会没数据呢？）

                        //处理数据
                        clientBuffer.flip();
                        byte[] content = new byte[clientBuffer.limit()];
                        clientBuffer.get(content);
                        System.out.println(new String(content));
                        System.out.println("收到数据，来自" + ch.getRemoteAddress());

                        String response = "HTTP/1.1 200 OK\r\n Content-Length: 100\r\n\r\n Hello World"; //注意Content-Length前面的空格（换行后要有空格才可以正确识别）
                        ByteBuffer byteBuffer = ByteBuffer.wrap(response.getBytes());

                        while (byteBuffer.hasRemaining()) {
                            ch.write(byteBuffer); //非阻塞
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
