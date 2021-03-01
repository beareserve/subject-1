package com.study.netty.push.test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/**
 * 简单约定一个协议：客户端每次发送150字节的内容
 */
public class XySocketClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("localhost", 9999);
        OutputStream outputStream = socket.getOutputStream();

        byte[] request = new byte[150];
        byte[] userId = "19909090".getBytes();
        byte[] content = "我爱你cb可你不爱我我爱你cb可你不爱我我爱你cb可你不爱我我爱你cb可你不爱我".getBytes();
        System.arraycopy(userId, 0, request, 0, 8);
        System.arraycopy(content, 0, request,8, content.length);

        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
               try {
                   outputStream.write(request);
               } catch (IOException e) {
                   e.printStackTrace();
               } finally {
                   countDownLatch.countDown();
               }
            }).start();
        }

        countDownLatch.await();
        Thread.sleep(9000l);
        socket.close();
    }
}
