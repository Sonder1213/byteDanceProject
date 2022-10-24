package serializer.impl.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.objenesis.strategy.StdInstantiatorStrategy;
import warp.Request;
import warp.Response;

/**
* @description: KryoPool单例工厂
* @author hewei
* @date 2022/9/22 20:16
* @version 1.0
*/

public class KryoPoolFactory {
    private static volatile KryoPoolFactory poolFactory = null;
    private final KryoFactory kryoFactory = new KryoFactory() {
        @Override
        public Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.register(Request.class);
            kryo.register(Response.class);
            Kryo.DefaultInstantiatorStrategy strategy = (Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy();
            strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };
    private final KryoPool pool = new KryoPool.Builder(kryoFactory).build();

    private KryoPoolFactory() {

    }

    public static KryoPool getKryoPool() {
        if (poolFactory == null) {
            synchronized (KryoPoolFactory.class) {
                if (poolFactory == null) {
                    poolFactory = new KryoPoolFactory();
                }
            }
        }
        return poolFactory.getPool();
    }

    private KryoPool getPool() {
        return pool;
    }
}
