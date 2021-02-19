package com.study.hc.net.nio.xy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

//todo NIO selector 多路复用reactor线程模型
public class NIOServerV3 {

    /**
     * 处理业务操作的线程池
     */
    private static ExecutorService workPool = Executors.newCachedThreadPool();


    /**
     * 封装了selector.select()等事件轮询的代码
     *
     * todo（相当于Netty中的EventLoopGroup的概念吧）
     */
    abstract class ReactorThread extends Thread{

        Selector selector;
        LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

        //selector监听到有事件后，调用这个方法
        public abstract void handler(SelectableChannel channel) throws Exception;

        private ReactorThread() throws IOException {
            selector = Selector.open();
        }

        volatile boolean running = false;

        @Override
        public void run() {

            while (running) {
                try {
                    //执行队列中的任务
                    Runnable task;
                    while ((task = taskQueue.poll()) != null) {
                        task.run();
                    }
                    selector.select(1000);

                    Set<SelectionKey> selected = selector.selectedKeys();

                    Iterator<SelectionKey> iter = selected.iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        int readyOps = key.readyOps();

                        //处理Read和Accept两个事件
                        if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
                            try {
                                SelectableChannel channel = (SelectableChannel) key.attachment(); //key.attachment()拿到对应的通道连接
                                channel.configureBlocking(false);
                                handler(channel);

                                if (!channel.isOpen()) {
                                    key.cancel(); //如果关闭了，就取消这个key的订阅（此方法是将本SelectionKey置为无效）
                                }
                            } catch (Exception e) {
                                key.cancel(); //有异常，取消key订阅
                            }
                        }
                    }
                    selector.selectNow();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private SelectionKey register(SelectableChannel channel) throws ExecutionException, InterruptedException {
            /**
             * 为什么register要以任务提交的形式，让reactor线程去处理？
             * 因为线程在执行channel注册到selector的过程中，会和调用selector.select()方法的线程争用同一把锁
             * 而select()方法是在eventLoop（即reactor）中通过while循环调用的，争抢的可能性很高，为了让register能更快执行，就放到同一个线程来处理
             */
            FutureTask<SelectionKey> futureTask = new FutureTask<>(() -> channel.register(selector, 0, channel));
            taskQueue.add(futureTask);
            return futureTask.get();
        }

        private void doStart() {
            if (!running) {
                running = true;
                start();
            }
        }
    }

    private ServerSocketChannel serverSocketChannel;

    private ReactorThread[] mainReactor = new ReactorThread[1]; //accept处理reactor线程
    private ReactorThread[] subReactor = new ReactorThread[8]; //i/o处理reactor线程

    private void newGroup() throws IOException {
        // 创建IO线程，负责处理连接客户端以后socketChannel的IO读写
        for (int i = 0; i < subReactor.length; i++) {
            subReactor[i] = new ReactorThread() {
                @Override
                public void handler(SelectableChannel channel) throws Exception {
                    SocketChannel ch = (SocketChannel) channel; //爷爷强转成孙子了
                    ByteBuffer requestBuffer = ByteBuffer.allocate(1024);
                    while (ch.isOpen() && ch.read(requestBuffer) != -1) {
                        // 长连接情况下，需要手动判断数据有没有读取结束（此处做一个简单判断：超过0字节就认为请求结束）
                        if (requestBuffer.position() > 0) break;
                    }

                    if (requestBuffer.position() == 0) return; //如果没数据了，则不继续后面的处理

                    requestBuffer.flip();
                    byte[] content = new byte[requestBuffer.limit()];
                    requestBuffer.get(content);
                    System.out.println(new String(content));
                    System.out.println(Thread.currentThread().getName() + "收到数据，来自" + ch.getRemoteAddress());

                    workPool.submit(() -> {
                        //TODO 业务处理部分
                    });

                    String response = "HTTP/1.1 200 OK \r\n Content-Length: 100 \r\n\r\n Hello XiaoCaiJi";
                    ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
                    while (buffer.hasRemaining()) {
                        ch.write(buffer);
                    }
                }
            };
        }

        for (int i = 0; i < mainReactor.length; i++) {
            mainReactor[i] = new ReactorThread() {
                AtomicInteger incr = new AtomicInteger(0);
                @Override
                public void handler(SelectableChannel channel) throws Exception {
                    //只做请求分发逻辑，不读取数据
                    ServerSocketChannel ch = (ServerSocketChannel) channel;
                    SocketChannel socketChannel = ch.accept();
                    socketChannel.configureBlocking(false);

                    //收到连接建立的通知后，分发给I/O线程去处理
                    int index = incr.getAndIncrement() % subReactor.length;
                    ReactorThread workEventLoop = subReactor[index];

                    workEventLoop.doStart();

                    SelectionKey selectionKey = workEventLoop.register(socketChannel);
                    selectionKey.interestOps(SelectionKey.OP_READ);
                    System.out.println(Thread.currentThread().getName() + "收到新连接：" + socketChannel.getRemoteAddress());

                }
            };
        }
    }

    /**
     * 初始化channel，并且绑定一个eventLoop线程
     */
    private void initAndRegister() throws IOException, ExecutionException, InterruptedException {
        //1、创建serverSocketChannel
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        //2、将serverSocketChannel注册到selector
        int index = new Random().nextInt(mainReactor.length);
        mainReactor[index].doStart();
        SelectionKey selectionKey = mainReactor[index].register(serverSocketChannel);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
    }

    private void bind() throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(8080));
        System.out.println("启动完成，端口8080");
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        NIOServerV3 nioServerV3 = new NIOServerV3();
        nioServerV3.newGroup();
        nioServerV3.initAndRegister();
        nioServerV3.bind();
    }

    //我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里我爱的人，到底在哪里
//    public static void main(String[] args) throws IOException {
//        ServerSocketChannel channel = ServerSocketChannel.open(); //绑定监听端口的通道对象，通过accept()方法获得具体通道连接
//        channel.configureBlocking(false);
//
//        Selector selector = Selector.open();
//        SelectionKey selectionKey = channel.register(selector, 0, channel); //将serverSocketChannel注册到selector
//        selectionKey.interestOps(SelectionKey.OP_ACCEPT); //对serverSocketChannel上面的accept事件感兴趣（serverSocketChannel只支持accept操作）
//
////        channel.bind(new InetSocketAddress(8080));
//        channel.socket().bind(new InetSocketAddress(8080));
//        System.out.println("启动成功");
//        while (true) {
//            selector.select();
//            Set<SelectionKey> selectionKeys = selector.selectedKeys();
//            Iterator<SelectionKey> iter = selectionKeys.iterator();
//
//            while (iter.hasNext()) {
//                SelectionKey key = iter.next();
//                iter.remove();
//
//                //处理连接事件
//                if (key.isAcceptable()) {
//                    ServerSocketChannel server = (ServerSocketChannel) key.attachment();
//                    SocketChannel ch = server.accept();
//                    ch.configureBlocking(false);
//                    ch.register(selector, SelectionKey.OP_READ, ch); //将拿到的客户端连接通道，注册到selector上面
//                    System.out.println("收到新连接：" + ch.getRemoteAddress());
//                }
//
//                //处理消息接收事件
//                if (key.isReadable()) {
//                    SocketChannel ch = (SocketChannel) key.attachment();
//                    try {
//                        ByteBuffer clientBuffer = ByteBuffer.allocateDirect(1024);
//                        while (ch.isOpen() && ch.read(clientBuffer) != -1) { //不判断isOpen，后面操作socketChannel会报错连接不存在
//                            if (clientBuffer.position() > 0) break;
//                        }
//
//                        if (clientBuffer.position() == 0) continue; //没数据了，不进行后面的处理（但啥情况下会没数据呢？）
//
//                        //处理数据
//                        clientBuffer.flip();
//                        byte[] content = new byte[clientBuffer.limit()];
//                        clientBuffer.get(content);
//                        System.out.println(new String(content));
//                        System.out.println("收到数据，来自" + ch.getRemoteAddress());
//
//                        String response = "HTTP/1.1 200 OK\r\n Content-Length: 100\r\n\r\n Hello World"; //注意Content-Length前面的空格（换行后要有空格才可以正确识别）
//                        ByteBuffer byteBuffer = ByteBuffer.wrap(response.getBytes());
//
//                        while (byteBuffer.hasRemaining()) {
//                            ch.write(byteBuffer); //非阻塞
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
}
