package com.study.lock.lock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class MyLockDemoCAS {

    private int v = 0;
    private static Unsafe unsafe;

    private static long offset;
    static {
        try {
//            Field field = MyLockDemoCAS.class.getDeclaredField("unsafe");
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
//            unsafe = (Unsafe) field.get("theUnsafe");
            unsafe = (Unsafe) field.get(null);
            offset = unsafe.objectFieldOffset(MyLockDemoCAS.class.getDeclaredField("v"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void addV() {
        int current;

        do {
            current = unsafe.getIntVolatile(this, offset);
        } while (!unsafe.compareAndSwapInt(this, offset, current, current + 1));
    }

    public static void main(String[] args) throws InterruptedException {
        MyLockDemoCAS myLockDemoCAS = new MyLockDemoCAS();
        for (int i = 0; i < 2; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 10000; j++) {
                        myLockDemoCAS.addV();
                    }
                }
            }).start();
        }
        Thread.sleep(2000l);
        System.out.println(myLockDemoCAS.v);
    }
}
