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
 * @description: TODO
 * @date 2022/10/21 21:59
 */

public class Test02 {

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
                if (count > 50000) arr.add(info);
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

        ThreadPoolExecutor executor = new ThreadPoolExecutor(1000, 2000, 1000, TimeUnit.MILLISECONDS
                , new ArrayBlockingQueue<>(5000, true), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(100);
        AtomicInteger temp = new AtomicInteger(50000);
        scheduledThreadPool.scheduleAtFixedRate(() -> {
            int count = 850;
            while (count-- > 0 && temp.getAndDecrement() > 0) {
                int finalTemp = temp.get();
                executor.execute(() -> {
                    NioEventLoopGroup group = new NioEventLoopGroup(1);
                    try {
                        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 11000);
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
                        group.shutdownGracefully();
                    }
                });
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}
