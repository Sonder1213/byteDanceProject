package com.ustc.hewei;

import enumeration.MyError;
import enumeration.PackageType;
import exception.MyException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import serializer.impl.kryo.KryoSerializer;
import warp.Request;
import warp.Response;

import java.util.Arrays;
import java.util.UUID;

/**
 * @author hewei
 * @version 1.0
 * @description: TODO
 * @date 2022/10/24 21:13
 */

public class Test04 {

    public static void main(String[] args) {
        Request msg = new Request();
        msg.setTicketId(1L);
        msg.setId(UUID.randomUUID().toString());
        msg.setToken("8324b8f4-9193-41a7-b45d-58aea8ce5abf");
        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[] bytes = kryoSerializer.serializer(msg);
        ByteBuf buffer = Unpooled.buffer(100, 10240);
        buffer.writeInt(0xCAFEBABE);
        buffer.writeInt(0);
        buffer.writeInt(1);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
        byte[] res = new byte[buffer.readableBytes()];
        buffer.readBytes(res);
        System.out.println(Arrays.toString(res));
    }
}
