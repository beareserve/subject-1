package com.study.thread.future.service;

import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

public class XyFutureTask<T> implements Runnable, Future {

    // 1推理FutureTask的实现：

    private Callable<T> callable;
    T result;
    volatile String state;
    LinkedBlockingQueue<Thread> waiters = new LinkedBlockingQueue<>();//定义一个存储等待者的集合

    public XyFutureTask(Callable callable) {
        this.callable = callable;
    }

    @Override
    public void run() {
        try {
            result = callable.call();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            state = "END";
        }

        Thread waiter = waiters.poll();
        while (waiter != null) {
            LockSupport.unpark(waiter);
            waiter = waiters.poll();
        }

    }
    @Override
    public T get() throws InterruptedException, ExecutionException {
        if ("END".equals(state)) {
            return result;
        }

        waiters.offer(Thread.currentThread());
        while (!"END".equals(state)) {
            LockSupport.park();
        }

        return result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }



    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
