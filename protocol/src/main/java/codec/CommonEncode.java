package codec;

import enumeration.MyError;
import enumeration.PackageType;
import exception.MyException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializer.CommonSerializer;
import serializer.impl.kryo.KryoSerializer;
import warp.Request;
import warp.Response;

/**
 * @author hewei
 * @version 1.0
 * @description: 编码器
 * @date 2022/10/21 10:44
 */

public class CommonEncode extends MessageToByteEncoder {
    private final int MAGIC_NUMBER = 0xCAFEBABE;
    private final Logger logger = LoggerFactory.getLogger(CommonEncode.class);
    private final CommonSerializer serializer;

    public CommonEncode(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    public CommonEncode() {
        this.serializer = new KryoSerializer();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC_NUMBER);
        if (msg instanceof Request) {
            out.writeInt(PackageType.REQUEST.getCode());
        } else if (msg instanceof Response) {
            out.writeInt(PackageType.RESPONSE.getCode());
        } else {
            logger.error("未知的包的类型 {}", msg.getClass());
            throw new MyException(MyError.UNKNOWN_PACKAGE, "在编码时遇到未知的包的类型");
        }
        out.writeInt(serializer.getCode());
        byte[] data = this.serializer.serializer(msg);
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
