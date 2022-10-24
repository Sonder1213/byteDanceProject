package com.ustc.hewei;

import codec.CommonDecode;
import codec.CommonEncode;
import com.ustc.hewei.netty.handler.ClientHandler2;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import warp.Request;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hewei
 * @version 1.0
 * @description: TODO
 * @date 2022/10/22 16:08
 */

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class Benchmark {
    private ClientHandler2 handler;
    private List<String> tokens;
    AtomicInteger temp = new AtomicInteger(100000);

    @org.openjdk.jmh.annotations.Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void test() {
        int i = temp.decrementAndGet();
        if (i >= 0) {
            Request request = new Request();
            request.setToken(tokens.get(i));
            request.setId(UUID.randomUUID().toString());
            request.setTicketId(1L);
            handler.sendRequest(request);
        }
    }

    public static void main(String[] args) throws Exception {
        // 使用一个单独进程执行测试，执行5遍warmup，然后执行3遍测试
        Options opt = new OptionsBuilder().include(Benchmark.class.getSimpleName()).forks(1).warmupIterations(2)
                .measurementIterations(2).output("/Users/hewei/java-project/byteDanceProject01/client/src/main/resources/Benchmark.json").build();
        new Runner(opt).run();
    }

    @Setup
    public void init() {
        handler = new ClientHandler2();
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            InetSocketAddress address = new InetSocketAddress("127.0.0.1", 11000);
            Bootstrap bootstrap = new Bootstrap().channel(NioSocketChannel.class)
                    .group(group)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new CommonEncode());
                    pipeline.addLast(new CommonDecode());
                    pipeline.addLast(handler);
                }
            }).connect(address).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File source = new File("/Users/hewei/java-project/byteDanceProject01/client/src/main/resources/tokens.txt");
        tokens = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new FileReader(source));
            String info = null;
            int count = 0;
            while (count < 100000) {
                info = br.readLine();
                ++count;
                tokens.add(info);
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
    }
}
