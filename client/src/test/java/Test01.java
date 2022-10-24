import codec.CommonDecode;
import codec.CommonEncode;
import com.ustc.hewei.netty.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import warp.Request;
import warp.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hewei
 * @version 1.0
 * @description: 短连接test
 * @date 2022/10/21 16:16
 */

public class Test01 {
    private static final Logger logger = LoggerFactory.getLogger(Test01.class);

    public static void main(String[] args) throws InterruptedException {
        File source = new File("/Users/hewei/java-project/byteDanceProject01/client/src/main/resources/tokens.txt");
        List<String> arr = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new FileReader(source));
            String info = null;
            int count = 0;
            while (count < 100000) {
                info = br.readLine();
                ++count;
                arr.add(info);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(400, 400, 1000, TimeUnit.MILLISECONDS
                , new ArrayBlockingQueue<>(4000, true), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(100);
        AtomicInteger temp = new AtomicInteger(100000);
        scheduledThreadPool.scheduleAtFixedRate(() -> {
            int count = 1700;
            while (count-- > 0 && temp.getAndDecrement() > 0) {
                int finalTemp = temp.get();
                executor.execute(() -> {
                    NioEventLoopGroup group = new NioEventLoopGroup(1);
                    try {
                        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 6666);
                        Bootstrap bootstrap = new Bootstrap().channel(NioSocketChannel.class)
                                .group(group)
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                                .option(ChannelOption.TCP_NODELAY, true);
                        Request request = new Request();
                        request.setTicketId(1L);
                        request.setId(UUID.randomUUID().toString());
                        request.setToken(arr.get(finalTemp));
                        final ClientHandler handler = new ClientHandler(request);
                        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                pipeline.addLast(new CommonEncode());
                                pipeline.addLast(new CommonDecode());
                                pipeline.addLast(handler);
                            }
                        }).connect(address).sync();
                        Response response = handler.getResponse();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        group.shutdownGracefully(0, 0, TimeUnit.MILLISECONDS);
                    }
                });
            }
        }, 2, 1, TimeUnit.SECONDS);
    }
}
