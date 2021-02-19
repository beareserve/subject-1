package com.study.hc.net.netty.demo;


/**
 * 自己写的垃圾，假责任链代码
 *
 * 只是顺序执行了处理器代码，但各处理器之间的参数并无交互
 *
 * 老师的代码是每个处理器里调用下一个处理器方法，调用时参数进行了交互
 * 我的代码各处理器完全不相干，所以要想实现同样效果，应该在。。。。。。想不出来。。。
 */
public class XyPipelineDemo {

    HandlerContext context = new HandlerContext(new AbstractXyHandler() {
        @Override
        void handler(String message) {
            System.out.println("小火车嘟嘟嘟~~~~~");
        }
    });
    public static void main(String[] args) {
        XyPipelineDemo xyPipelineDemo = new XyPipelineDemo();
        xyPipelineDemo.addNext(new XyHandler1());
        xyPipelineDemo.addNext(new XyHandler2());
        xyPipelineDemo.addNext(new XyHandler1());
        xyPipelineDemo.addNext(new XyHandler2());

        xyPipelineDemo.process("开始了");
    }

    private void process(String message) {
        if (context.handler != null) {
            context.handler.handler(message);
        }
        HandlerContext temp = context;
        while (temp.next != null) {
            temp.next.handler.handler(message);
            temp = temp.next;
        }
    }

    void addNext(AbstractXyHandler handler) {
        HandlerContext temp = context;
        while (temp.next != null) {
            temp = temp.next;
        }
        temp.next = new HandlerContext(handler);
    }
}


class HandlerContext {

    HandlerContext next;
    AbstractXyHandler handler;

    public HandlerContext(AbstractXyHandler handler) {
        this.handler = handler;
    }

    void runNext(String message) {
        this.next.handler.handler(message);
    }
}

abstract class AbstractXyHandler {
    abstract void handler(String message);
}

class XyHandler1 extends AbstractXyHandler {

    @Override
    void handler(String message) {
        System.out.println(message = message + "加上handler1的小尾巴");
    }
}

class XyHandler2 extends AbstractXyHandler {

    @Override
    void handler(String message) {
        System.out.println(message = message + "加上handler2的小尾巴");
    }
}