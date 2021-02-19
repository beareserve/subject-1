package com.study.hc.net.nio.xy;

import java.nio.ByteBuffer;

public class BufferDemo {


    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);

        System.out.println(String.format("初始化：capacity容量大小：%s, position位置：%s，limit限制：%s", byteBuffer.capacity(), byteBuffer.position(), byteBuffer.limit()));

        byteBuffer.put((byte)1);
        byteBuffer.put((byte)2);
        byteBuffer.put((byte)3);

        System.out.println(String.format("写入3字节后：capacity容量大小：%s, position位置：%s，limit限制：%s", byteBuffer.capacity(), byteBuffer.position(), byteBuffer.limit()));


        //转为读模式,要显示调用一下flip()方法
        byteBuffer.flip();
        System.out.println(String.format("转为读模式后：capacity容量大小：%s, position位置：%s，limit限制：%s", byteBuffer.capacity(), byteBuffer.position(), byteBuffer.limit()));

        byte a = byteBuffer.get();
        System.out.println(a);
        byte b = byteBuffer.get();
        System.out.println(b);
        System.out.println(String.format("读取2字节后：capacity容量大小：%s, position位置：%s，limit限制：%s", byteBuffer.capacity(), byteBuffer.position(), byteBuffer.limit()));
//        byte c = byteBuffer.get();
//        System.out.println(c);
//        System.out.println(String.format("读取3字节后，最终：capacity容量大小：%s, position位置：%s，limit限制：%s", byteBuffer.capacity(), byteBuffer.position(), byteBuffer.limit()));

        //清除已读数据
        byteBuffer.compact();
        System.out.println(String.format("清除已读取数据后：capacity容量大小：%s, position位置：%s，limit限制：%s", byteBuffer.capacity(), byteBuffer.position(), byteBuffer.limit()));

        byteBuffer.put((byte)1);
        byteBuffer.put((byte)2);
        byteBuffer.put((byte)3);
        System.out.println(String.format("清除已读取数据再放入3字节：capacity容量大小：%s, position位置：%s，limit限制：%s", byteBuffer.capacity(), byteBuffer.position(), byteBuffer.limit()));

    }
}
