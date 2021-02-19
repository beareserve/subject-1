package com.study.lock.xy;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * ʵ���Լ�������������
 */
public class XyLock implements Lock {

    XyAqs aqs = new XyAqs(){

        @Override
        public boolean tryAcquire() {
            return owner.compareAndSet(null, Thread.currentThread());
        }

        @Override
        public boolean tryRelease() {
            return owner.compareAndSet(Thread.currentThread(), null);
        }
    };

    @Override
    public boolean tryLock() {
        return aqs.tryAcquire();
    }
    //����ж�һ������״̬��ӵ����
    @Override
    public void lock() {
        aqs.acquire();
    }

    @Override
    public void unlock() {
        aqs.release();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }


    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }


    @Override
    public Condition newCondition() {
        return null;
    }
}
