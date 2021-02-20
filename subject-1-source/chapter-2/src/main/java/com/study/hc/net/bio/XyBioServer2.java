package com.study.hc.net.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XyBioServer2 {

    static ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("服务器启动成功");
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("收到新的连接请求：" + socket.toString());
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (!serverSocket.isClosed()) {
                                BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                String line;
                                while ((line = r.readLine()) != null) {
                                    if (line.length() == 0) {
                                        break;
                                    }
                                    System.out.println("收到数据：" + line + " 来自：" + socket.toString());
                                }
                            }
                        } catch (Exception e) {

                        } finally {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
