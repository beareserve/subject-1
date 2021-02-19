package com.study.lock.xy;

import java.util.concurrent.LinkedBlockingQueue;

// 循环屏障(栅栏)，示例：数据库批量插入
// 游戏大厅... 5人组队打副本
public class CyclicBarrierTest1 {
    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue<String> sqls = new LinkedBlockingQueue<>();
        // 任务1+2+3...1000  拆分为100个任务（1+..10,  11+20） -> 100线程去处理。

        // 每当有4个线程处于await状态的时候，则会触发barrierAction执行
        /**
         * , new Runnable() {
         *             @Override
         *             public void run() {
         *                 // 这是每满足4次数据库操作，就触发一次批量执行
         *                 System.out.println("有4个线程执行了，开始批量插入： " + Thread.currentThread());
         *                 for (int i = 0; i < 4; i++) {
         *                     System.out.println("这个是啥" + sqls.poll());
         *                 }
         *             }
         *         }
         */
        XyCyclicBarrier barrier = new XyCyclicBarrier(2);


        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    System.out.println(("data - " + Thread.currentThread() + "开始进入线程执行片段")); // 缓存起来
//                    Thread.sleep(1000L); // 模拟数据库操作耗时
                    barrier.await(); // 等待栅栏打开,有4个线程都执行到这段代码的时候，才会继续往下执行
                    System.err.println(Thread.currentThread() + "插入完毕");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Thread.sleep(2000);
    }
}