package com.study.lock.xy;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class XyCyclicBarrier {
    private class LoopGeneration {

    }

    private final int parties;
//    private final Runnable commend;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition trip = lock.newCondition();
    private LoopGeneration generation = new LoopGeneration();

    private boolean nextGeneration = false;
    private int count;

    public XyCyclicBarrier(int parties) {
        this.parties = parties;
        this.count = parties;
    }

    private void nextGeneration() {
        trip.signalAll();
        count = parties;
        generation = new LoopGeneration();
    }
    public int await() {

        System.out.println(Thread.currentThread().getName() + "�ߵ�դ��ǰ��");
        final ReentrantLock lock = this.lock;

        lock.lock();

        try {
            LoopGeneration g = generation;
            int index = --count;
            if (index == 0) {
                System.out.println(Thread.currentThread().getName() + "�ﵽ�������ޣ�����դ�����У����Ǵ󹦳�");
                nextGeneration();
                nextGeneration = true;
                return 0;
            }

            while (true) {
                try {
                    System.out.println(Thread.currentThread().getName() + "�ȴ�����ͬ�����");
                    trip.await();
                } catch (InterruptedException ie) {

                }
//                if (nextGeneration) {
//                    nextGeneration = false;
//                    return index;
//                }
                if (g != generation) {
                    System.err.println(Thread.currentThread().getName() + "������һ��ѭ���ȴ�");
                    return index;
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
