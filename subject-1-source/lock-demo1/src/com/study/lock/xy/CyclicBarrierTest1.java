package com.study.lock.xy;

import java.util.concurrent.LinkedBlockingQueue;

// ѭ������(դ��)��ʾ�������ݿ���������
// ��Ϸ����... 5����Ӵ򸱱�
public class CyclicBarrierTest1 {
    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue<String> sqls = new LinkedBlockingQueue<>();
        // ����1+2+3...1000  ���Ϊ100������1+..10,  11+20�� -> 100�߳�ȥ����

        // ÿ����4���̴߳���await״̬��ʱ����ᴥ��barrierActionִ��
        /**
         * , new Runnable() {
         *             @Override
         *             public void run() {
         *                 // ����ÿ����4�����ݿ�������ʹ���һ������ִ��
         *                 System.out.println("��4���߳�ִ���ˣ���ʼ�������룺 " + Thread.currentThread());
         *                 for (int i = 0; i < 4; i++) {
         *                     System.out.println("�����ɶ" + sqls.poll());
         *                 }
         *             }
         *         }
         */
        XyCyclicBarrier barrier = new XyCyclicBarrier(2);


        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    System.out.println(("data - " + Thread.currentThread() + "��ʼ�����߳�ִ��Ƭ��")); // ��������
//                    Thread.sleep(1000L); // ģ�����ݿ������ʱ
                    barrier.await(); // �ȴ�դ����,��4���̶߳�ִ�е���δ����ʱ�򣬲Ż��������ִ��
                    System.err.println(Thread.currentThread() + "�������");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Thread.sleep(2000);
    }
}