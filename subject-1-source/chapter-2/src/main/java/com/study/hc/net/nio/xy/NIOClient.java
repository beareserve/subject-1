package com.study.hc.net.nio.xy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NIOClient {

    public static void main(String[] args) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress("localhost", 8080));

        while (!channel.finishConnect()) {
            Thread.yield();
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入：");
        String msg = scanner.nextLine();
        ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
        while (byteBuffer.hasRemaining()) {
            channel.write(byteBuffer);
        }

        System.out.println("收到服务器响应");
        ByteBuffer serverBuffer = ByteBuffer.allocate(1024);

        while (channel.isOpen() && channel.read(serverBuffer) != -1) {
            if (serverBuffer.position() > 0) {
                break;
            }
        }
        serverBuffer.flip();
        byte[] content = new byte[serverBuffer.limit()];
        serverBuffer.get(content);
        System.out.println(new String(content));
        scanner.close();
        channel.close();
    }
}
