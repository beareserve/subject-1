package com.study.netty.push.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;

public class XySocketServer {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(9999);

        while (true) {
            final Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            while (true) {
                byte[] request = new byte[300];
                int read = inputStream.read(request);
                if (read == -1) {
                    break;
                }
                String content = new String(request);
                System.out.println(content);
            }
        }
    }
}
