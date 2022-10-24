package serializer.impl.protostuff;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.ObjenesisStd;
import serializer.CommonSerializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @description: ProtoBuffer序列化
* @author hewei
* @date 2022/9/22 21:14
* @version 1.0
*/

public class ProtostuffSerializer implements CommonSerializer {
    private final Map<Class<?>, Schema<?>> schemaMap;
    private final ObjenesisStd objenesisStd;

    public ProtostuffSerializer() {
        schemaMap = new ConcurrentHashMap<>();
        objenesisStd = new ObjenesisStd(true);
    }

    public <T> Schema<T> getSchema(Class<T> clazz) {
        return (Schema<T>) schemaMap.computeIfAbsent(clazz, RuntimeSchema::createFrom);
    }

    @Override
    public <T> byte[] serializer(T obj) {
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = (Schema<T>) getSchema(obj.getClass());
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> Object deSerializer(byte[] data, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        // 下面这种方法对于没有无参构造或者构造器是私有的类并不适用
        // T t = schema.newMessage();
        T message = objenesisStd.newInstance(clazz);
        ProtostuffIOUtil.mergeFrom(data, message, schema);
        return message;
    }

    @Override
    public int getCode() {
        return 2;
    }
}
