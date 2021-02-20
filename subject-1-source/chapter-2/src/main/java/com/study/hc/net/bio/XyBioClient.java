package com.study.hc.net.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class XyBioClient {


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8080);
        try {
            OutputStream out = socket.getOutputStream();
            System.out.println("请写下你想说的话：");
//            while (true) {
                Scanner scanner = new Scanner(System.in);
                String msg = scanner.nextLine();
                out.write(msg.getBytes());
                scanner.close();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();

        }
    }
}
