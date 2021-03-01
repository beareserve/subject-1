package com.study.lock.xy;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

/**
 * 抽象队列同步器
 * state, owner, waiters
 */
public class XyAqs {

    // acquire、 acquireShared ： 定义了资源争用的逻辑，如果没拿到，则等待。
    // tryAcquire、 tryAcquireShared ： 实际执行占用资源的操作，如何判定一个由使用者具体去实现。
    // release、 releaseShared ： 定义释放资源的逻辑，释放之后，通知后续节点进行争抢。
    // tryRelease、 tryReleaseShared： 实际执行资源释放的操作，具体的AQS使用者去实现。

    public volatile AtomicReference<Thread> owner = new AtomicReference<>();
    public volatile AtomicInteger state = new AtomicInteger(0);

    public volatile LinkedBlockingQueue<Thread> waiters = new LinkedBlockingQueue<>();

    //共享资源逻辑
    public int tryAcquireShared() {
        throw new UnsupportedOperationException();
    }

    public void acquireShared() {
        boolean notAdd2Waiters = true;
        while (tryAcquireShared() < 0) {
            //没拿到锁，加入到等待集合
            if (notAdd2Waiters) {
                waiters.offer(Thread.currentThread());
                notAdd2Waiters = false;
            }
            //阻塞 挂起当前线程，不继续往下跑了
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



    //独占资源逻辑
    public void acquire() {
        boolean notAdd2Waiters = true;
        while (!tryAcquire()) {
            //没拿到锁，加入到等待集合
            if (notAdd2Waiters) {
                waiters.offer(Thread.currentThread());
                notAdd2Waiters = false;
            }
            //阻塞 挂起当前线程，不继续往下跑了
            LockSupport.park();
        }
        waiters.remove(Thread.currentThread()); //当前线程拿到锁，从等待队列中移除
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
