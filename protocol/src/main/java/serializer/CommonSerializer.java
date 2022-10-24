package serializer;

import serializer.impl.kryo.KryoSerializer;
import serializer.impl.protostuff.ProtostuffSerializer;

/**
* @description: 序列化类型接口
* @author hewei
* @date 2022/9/5 19:47
* @version 1.0
*/

public interface CommonSerializer {

    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 1:
                return new KryoSerializer();
            case 2:
                return new ProtostuffSerializer();
            default:
                return null;
        }
    }

    <T> byte[] serializer(T obj);

    <T> Object deSerializer(byte[] data, Class<T> clazz);

    int getCode();
}
