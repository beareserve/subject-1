package com.study.lock.aqs.jucdemo.cyclicbarrier;

import com.study.lock.aqs.source.CyclicBarrierSource;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

// ѭ������(դ��)��ʾ�������ݿ���������
// ��Ϸ����... 5����Ӵ򸱱�
public class CyclicBarrierTest {
    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue<String> sqls = new LinkedBlockingQueue<>();
        // ����1+2+3...1000  ���Ϊ100������1+..10,  11+20�� -> 100�߳�ȥ����

        // ÿ����4���̴߳���await״̬��ʱ����ᴥ��barrierActionִ��
        CyclicBarrierSource barrier = new CyclicBarrierSource(2, new Runnable() {
            @Override
            public void run() {
                // ����ÿ����4�����ݿ�������ʹ���һ������ִ��
//                System.out.println("��4���߳�ִ���ˣ���ʼ�������룺 " + Thread.currentThread());
//                for (int i = 0; i < 4; i++) {
//                    System.out.println(sqls.poll());
//                }
            }
        });

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    System.out.println(("data - " + Thread.currentThread() + "�ڵȴ��ſ�դ��")); // ��������
                    Thread.sleep(1000L); // ģ�����ݿ������ʱ
                    barrier.await(); // �ȴ�դ����,��4���̶߳�ִ�е���δ����ʱ�򣬲Ż��������ִ��
                    System.out.println(Thread.currentThread() + "�������");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Thread.sleep(2000);
    }
}