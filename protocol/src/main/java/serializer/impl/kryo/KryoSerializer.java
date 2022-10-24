package serializer.impl.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import enumeration.SerializerType;
import serializer.CommonSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
* @description: Kryo序列化方式
* @author hewei
* @date 2022/9/22 20:24
* @version 1.0
*/

public class KryoSerializer implements CommonSerializer {
    private final KryoPool pool;

    public KryoSerializer() {
        pool = KryoPoolFactory.getKryoPool();
    }

    @Override
    public byte[] serializer(Object obj) {
        Kryo kryo = pool.borrow();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Output output = new Output(outputStream);
            kryo.writeObject(output, obj);
            output.close();
            return outputStream.toByteArray();
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pool.release(kryo);
        }
    }

    @Override
    public <T> Object deSerializer(byte[] data, Class<T> clazz) {
        Kryo kryo = pool.borrow();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        try {
            Input input = new Input(inputStream);
            T res = kryo.readObject(input, clazz);
            input.close();
            return res;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pool.release(kryo);
        }
    }

    @Override
    public int getCode() {
        return SerializerType.KRYO.getCode();
    }
}
