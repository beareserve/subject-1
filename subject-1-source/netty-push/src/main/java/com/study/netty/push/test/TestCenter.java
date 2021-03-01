package com.study.netty.push.test;

import com.study.netty.push.handler.WebSocketServerHandler;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// ��������ǣ���̨ϵͳͨ���ӿ����󣬰����ݶ�����Ӧ��MQ���У��������ͷ�������ȡ
public class TestCenter {
    // �˴�����һ���û�һ̨�豸�������û���ͨ��Ӧ���Ƕ����
    // TODO ��Ӧ����һ����ʱ�������ڼ��ʧЧ������(���ƻ����е�LRU�㷨����ʱ�䲻ʹ�ã����ó������һ���Ƿ�Ͽ���)��
    static ConcurrentHashMap<String, Channel> userInfos = new ConcurrentHashMap<String, Channel>();

    // ������Ϣ
    public static void saveConnection(String userId, Channel channel) {
        userInfos.put(userId, channel);
    }

    // �˳���ʱ���Ƴ���
    public static void removeConnection(Object userId) {
        if (userId != null) {
            userInfos.remove(userId.toString());
        }
    }

    final static byte[] JUST_TEST = new byte[1024];

    public static void startTest() {
        // ��һ��tony��
        System.arraycopy("tony".getBytes(), 0, JUST_TEST, 0, 4);
        final String sendmsg = System.getProperty("netease.server.test.sendmsg", "false");
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            try {
                // ѹ�����ԣ����û��������ȡ1/10���з���
                if (userInfos.isEmpty()) {
                    return;
                }
                int size = userInfos.size();
                ConcurrentHashMap.KeySetView<String, Channel> keySetView = userInfos.keySet();
                String[] keys = keySetView.toArray(new String[]{});
                System.out.println(WebSocketServerHandler.counter.sum() + " : ��ǰ�û�����" + keys.length);
                if (Boolean.valueOf(sendmsg)) { // �Ƿ�������
                    for (int i = 0; i < (size > 10 ? size / 10 : size); i++) {
                        // �ύ�������ִ��
                        String key = keys[new Random().nextInt(size)];
                        Channel channel = userInfos.get(key);
                        if (channel == null) {
                            continue;
                        }
                        if (!channel.isActive()) {
                            userInfos.remove(key);
                            continue;
                        }
                        channel.eventLoop().execute(() -> {
                            channel.writeAndFlush(new TextWebSocketFrame(new String(JUST_TEST))); // ����1024�ֽ�
                        });

                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 1000L, 2000L, TimeUnit.MILLISECONDS);
    }
}
