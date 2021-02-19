package com.study.juc.queue;

import java.util.concurrent.SynchronousQueue;

public class TestClass {

    public static void main(String[] args) throws Exception{

        //同步队列入栈/队列测试
        SynchronousQueue<Integer> queue = new SynchronousQueue<>(false);
//        SynchronousQueue<Integer> queue = new SynchronousQueue<>(true);
        new Thread(()->{
            try {
                queue.put(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(500);
        System.out.println(queue.take());
    }
}
