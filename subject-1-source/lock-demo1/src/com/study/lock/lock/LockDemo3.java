package com.study.lock.lock;

import com.study.lock.xy.XyLock;

import java.util.concurrent.locks.Lock;

public class LockDemo3 {
    volatile int i = 0;

    Lock lock = new XyLock();

    public void add() {
        lock.lock();
        try {
            // TODO  很多业务操作
            i++;
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LockDemo3 ld = new LockDemo3();

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                for (int j = 0; j < 10000; j++) {
                    ld.add();
                }
            }).start();
        }
        Thread.sleep(5000L);
        System.out.println(ld.i);
    }
}
