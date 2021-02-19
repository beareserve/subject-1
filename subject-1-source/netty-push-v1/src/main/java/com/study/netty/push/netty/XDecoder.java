package com.study.netty.push.netty;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class XDecoder extends ByteToMessageDecoder {
    static final int PACKET_SIZE = 220;

    // ������ʱ����û�д������������
    ByteBuf tempMsg = Unpooled.buffer();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("�յ���һ�����ݰ��������ǣ�" + in.readableBytes());
        // in ���������
        // out ��ճ��һ��ı��Ĳ�ֺ�Ľ����������

        // 1�� �ϲ�����
        ByteBuf message = null;
        int tmpMsgSize = tempMsg.readableBytes();
        // ����ݴ�����һ�����µ������ģ���ϲ�
        if (tmpMsgSize > 0) {
            message = Unpooled.buffer();
            message.writeBytes(tempMsg);
            message.writeBytes(in);
            System.out.println("�ϲ�����һ���ݰ����µĳ���Ϊ��" + tmpMsgSize + ",�ϲ��󳤶�Ϊ:" + message.readableBytes());
        } else {
            message = in;
        }

        // 2�� ��ֱ���
        // ��������£�һ������̶�����Ϊ3�����Ը��ݳ��������
        // i+1 i+1 i+1 i+1 i+1
        // ���̶����ȣ���ҪӦ�ò�Э����Լ�� ��μ��㳤��
        // ��Ӧ�ò��У����ݵ������ĵĳ��ȼ������ǣ��������Ľ��в�ֻ�ϲ�
        // dubbo rpcЭ�� = header(16) + body(���̶�)
        // header����ĸ��ֽ�����ʶbody
        // ���� = 16 + body����
        // 0xda, 0xbb ħ��


        int size = message.readableBytes();
        int counter = size / PACKET_SIZE;
        for (int i = 0; i < counter; i++) {
            byte[] request = new byte[PACKET_SIZE];
            // ÿ�δ��ܵ���Ϣ�ж�ȡ3���ֽڵ�����
            message.readBytes(request);

            // ����ֺ�Ľ������out�б��У����ɺ����ҵ���߼�ȥ����
            out.add(Unpooled.copiedBuffer(request));
        }

        // 3������ı��Ĵ�����
        // ��һ�����ģ� i+  �ݴ�
        // �ڶ������ģ� 1 ���һ��
        size = message.readableBytes();
        if (size != 0) {
            System.out.println("��������ݳ��ȣ�" + size);
            // ʣ���������ݷŵ�tempMsg�ݴ�
            tempMsg.clear();
            tempMsg.writeBytes(message.readBytes(size));
        }

    }

}
