import codec.CommonDecode;
import codec.CommonEncode;
import com.ustc.hewei.netty.handler.ClientHandler2;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import warp.Request;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hewei
 * @version 1.0
 * @description: 长连接test
 * @date 2022/10/22 15:23
 */

public class Test03 {

    public static void main(String[] args) {
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

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            InetSocketAddress address = new InetSocketAddress("127.0.0.1", 6667);
            Bootstrap bootstrap = new Bootstrap().channel(NioSocketChannel.class)
                    .group(group)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true);
            ClientHandler2 handler = new ClientHandler2();
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new CommonEncode());
                    pipeline.addLast(new CommonDecode());
                    pipeline.addLast(handler);
                }
            }).connect(address).sync().addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("连接成功");
                } else {
                    System.out.println("连接失败");
                }
            });

            ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
            AtomicInteger temp = new AtomicInteger(100000);
            scheduledThreadPool.scheduleAtFixedRate(() -> {
                int count = 1700;
                while (count-- > 0 && temp.getAndDecrement() > 0) {
                    int finalTemp = temp.get();
                    Request request = new Request();
                    request.setTicketId(1L);
                    request.setToken(arr.get(finalTemp));
                    request.setId(UUID.randomUUID().toString());
                    handler.sendRequest(request);
                    handler.add(request);
                }
            }, 1, 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
