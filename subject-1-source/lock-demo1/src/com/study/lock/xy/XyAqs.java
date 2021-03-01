package com.study.lock.xy;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

/**
 * �������ͬ����
 * state, owner, waiters
 */
public class XyAqs {

    // acquire�� acquireShared �� ��������Դ���õ��߼������û�õ�����ȴ���
    // tryAcquire�� tryAcquireShared �� ʵ��ִ��ռ����Դ�Ĳ���������ж�һ����ʹ���߾���ȥʵ�֡�
    // release�� releaseShared �� �����ͷ���Դ���߼����ͷ�֮��֪ͨ�����ڵ����������
    // tryRelease�� tryReleaseShared�� ʵ��ִ����Դ�ͷŵĲ����������AQSʹ����ȥʵ�֡�

    public volatile AtomicReference<Thread> owner = new AtomicReference<>();
    public volatile AtomicInteger state = new AtomicInteger(0);

    public volatile LinkedBlockingQueue<Thread> waiters = new LinkedBlockingQueue<>();

    //������Դ�߼�
    public int tryAcquireShared() {
        throw new UnsupportedOperationException();
    }

    public void acquireShared() {
        boolean notAdd2Waiters = true;
        while (tryAcquireShared() < 0) {
            //û�õ��������뵽�ȴ�����
            if (notAdd2Waiters) {
                waiters.offer(Thread.currentThread());
                notAdd2Waiters = false;
            }
            //���� ����ǰ�̣߳���������������
            LockSupport.park();
        }

        waiters.remove(Thread.currentThread());
    }

    public boolean tryReleaseShared() {
        throw new UnsupportedOperationException();
    }
    public void releaseShared() {
        if (tryReleaseShared()) {
            Iterator<Thread> iterator = waiters.iterator();
            while (iterator.hasNext()) {
                Thread next = iterator.next();
                LockSupport.unpark(next);
            }
        }
    }



    //��ռ��Դ�߼�
    public void acquire() {
        boolean notAdd2Waiters = true;
        while (!tryAcquire()) {
            //û�õ��������뵽�ȴ�����
            if (notAdd2Waiters) {
                waiters.offer(Thread.currentThread());
                notAdd2Waiters = false;
            }
            //���� ����ǰ�̣߳���������������
            LockSupport.park();
        }
        waiters.remove(Thread.currentThread()); //��ǰ�߳��õ������ӵȴ��������Ƴ�
    }
    public boolean tryAcquire() {
        return false;
    }
    public void release() {
        if (tryRelease()) {
            Iterator<Thread> iterator = waiters.iterator();
            while (iterator.hasNext()) {
                Thread next = iterator.next();
                LockSupport.unpark(next);
            }
        }
    }
    public boolean tryRelease() {
        throw new UnsupportedOperationException();
    }


    public AtomicReference<Thread> getOwner() {
        return owner;
    }

    public XyAqs setOwner(AtomicReference<Thread> owner) {
        this.owner = owner;
        return this;
    }

    public LinkedBlockingQueue<Thread> getWaiters() {
        return waiters;
    }

    public XyAqs setWaiters(LinkedBlockingQueue<Thread> waiters) {
        this.waiters = waiters;
        return this;
    }

    public AtomicInteger getState() {
        return state;
    }

    public XyAqs setState(AtomicInteger state) {
        this.state = state;
        return this;
    }
}
