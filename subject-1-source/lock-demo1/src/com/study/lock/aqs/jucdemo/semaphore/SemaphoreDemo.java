package com.study.lock.aqs.jucdemo.semaphore;

import java.util.Random;

// �ź�������
public class  SemaphoreDemo {
    public static void main(String[] args) {
        SemaphoreDemo semaphoreTest = new SemaphoreDemo();
        int N = 9;            // ��������
        NeteaseSemaphore semaphore = new NeteaseSemaphore(5); // ����������������������
        for (int i = 0; i < N; i++) {
            String vipNo = "vip-00" + i;
            new Thread(() -> {
                try {
                    semaphore.acquire(); // ��ȡ����

                    semaphoreTest.service(vipNo);

                    semaphore.release(); // �ͷ�����
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    // ���� ����5���߳� ͬʱ����
    public void service(String vipNo) throws InterruptedException {
        System.out.println("¥�ϳ���ӭ�ӹ��һλ��������" + vipNo + "��...");
        Thread.sleep(new Random().nextInt(3000));
        System.out.println("���͹�����ţ�������" + vipNo);
    }

}