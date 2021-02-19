package com.study.lock.aqs.jucdemo.cdl;

import com.study.lock.aqs.AQSdemo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

// CountDownLatch �Լ�ʵ��
public class CDLdemo {
    AQSdemo aqSdemo = new AQSdemo() {
        @Override
        public int tryAcquireShared() { // ����ǵ���0������ǰ�����߳�û׼������������Ϊ��Ҫ�ȴ�
            return this.getState().get() == 0 ? 1 : -1;
        }

        @Override
        public boolean tryReleaseShared() { // ����ǵ���0������ǰ�����߳�û׼���������򲻻�֪ͨ����ִ��
            return this.getState().decrementAndGet() == 0;
        }

//        @Override
//        public boolean tryReleaseShared() {
//            return getState().incrementAndGet() >= 0;
//        }
//
//        @Override
//        public int tryAcquireShared() {
//            while (true) {
//                int count = getState().get();
//                int n = count - 1;
//                if (count <= 0 || n < 0) {
//                    return -1;
//                }
//                if (getState().compareAndSet(count, n)) {
//                    return 1;
//                }
//                return -1;
//            }
//        }
    };

    public CDLdemo(int count) {
        aqSdemo.setState(new AtomicInteger(count));
    }

    public void await() {
        aqSdemo.acquireShared();
    }

    public void countDown() {
        aqSdemo.releaseShared();
    }

    public static void main(String[] args) throws InterruptedException {
        // һ�����󣬺�̨��Ҫ���ö���ӿ� ��ѯ����
        CountDownLatch cdLdemo = new CountDownLatch(10); // ������������ֵ
        for (int i = 0; i < 10; i++) { // �����Ÿ��̣߳����һ�����������
            int finalI = i;
            new Thread(() -> {
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("����" + Thread.currentThread() + ".��ִ�нӿ�-" + finalI +"������");
                cdLdemo.countDown(); // �������
                // ��Ӱ���������
            }).start();
        }

        cdLdemo.await(); // �ȴ�������Ϊ0
        System.out.println("ȫ��ִ�����.�����ٻ�����");

    }
}
