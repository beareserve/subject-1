package com.study.lock.xy;

import com.study.lock.xy.XyAqs;

/**
 * 自定义信号量实现
 */
public class XySemaphore {
    public XySemaphore(int cnt) {
        aqs.getState().set(cnt);
    }

    XyAqs aqs = new XyAqs(){
        @Override
        public boolean tryReleaseShared() {
            return getState().incrementAndGet() >= 0;
        }

        @Override
        public int tryAcquireShared() {
            while (true) {
                int count = getState().get();
                int n = count - 1;
                if (count <= 0 || n < 0) {
                    return -1;
                }
                if (getState().compareAndSet(count, n)) {
                    return 1;
                }
                return -1;
            }
        }
    };


    public void acquire() {
        aqs.acquireShared();
    }

    public void release() {
        aqs.releaseShared();
    }
}
