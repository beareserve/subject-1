package com.study.hc.net.nio.xy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

public class NIOServerV1 {


    private static ArrayList<SocketChannel> channels = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open(); //绑定监听端口的通道对象，通过accept()方法获得具体通道连接
        channel.configureBlocking(false);
//        channel.bind(new InetSocketAddress(8080));
        channel.socket().bind(new InetSocketAddress(8080));
        System.out.println("启动成功");
        while (true) {
            SocketChannel socketChannel = channel.accept(); //具体的客户端-服务端连接
            if (socketChannel != null) {
                System.out.println("收到新连接：" + socketChannel.getRemoteAddress());
                socketChannel.configureBlocking(false);
                channels.add(socketChannel);
            } else {
                Iterator<SocketChannel> iterator = channels.iterator();
                while (iterator.hasNext()) {
                    SocketChannel ch = iterator.next();
                    try {
                        ByteBuffer clientBuffer = ByteBuffer.allocateDirect(1024);
                        if (ch.read(clientBuffer) == 0) continue; //如果没数据了，则不继续后面的处理

                        while (ch.isOpen() && ch.read(clientBuffer) != -1) { //不判断isOpen，后面操作socketChannel会报错连接不存在
                            if (clientBuffer.position() > 0) break;
                        }

                        if (clientBuffer.position() == 0) continue;

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
