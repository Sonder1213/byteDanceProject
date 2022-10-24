package codec;

import enumeration.MyError;
import enumeration.PackageType;
import exception.MyException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializer.CommonSerializer;
import warp.Request;
import warp.Response;

import java.util.List;

/**
 * @author hewei
 * @version 1.0
 * @description: 解码器
 * @date 2022/10/21 10:45
 */

public class CommonDecode extends ReplayingDecoder {
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    private static final Logger logger = LoggerFactory.getLogger(CommonDecode.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magic = in.readInt();
        if (magic != MAGIC_NUMBER) {
            logger.error("未知的协议包 {}", magic);
            throw new MyException(MyError.UNKNOWN_PROTOCOL, "解码时收到未知识别的协议包");
        }
        int packageType = in.readInt();
        Class<?> packageClass;
        if (packageType == PackageType.REQUEST.getCode()) {
            packageClass = Request.class;
        } else if (packageType == PackageType.RESPONSE.getCode()) {
            packageClass = Response.class;
        } else {
            logger.error("未知的包的类型 {}", packageType);
            throw new MyException(MyError.UNKNOWN_PACKAGE, "解码时收到未知的包的类型");
        }
        int serializerType = in.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerType);
        if (serializer == null) {
            logger.error("未知的序列化类型 {}", serializerType);
            throw new MyException(MyError.UNKNOWN_SERIALIZER, "解码时收到未知的序列化类型");
        }
        int messageLength = in.readInt();
        byte[] data = new byte[messageLength];
        in.readBytes(data);
        out.add(serializer.deSerializer(data, packageClass));
    }
}
